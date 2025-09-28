package ru.ssau.tk._repfor2lab_._OOP_.functions;

import java.util.Arrays;

public class ArrayTabulatedFunction extends AbstractTabulatedFunction{

    private double[] xValues;
    private double[] yValues;

    public ArrayTabulatedFunction(double[] xValues, double[] yValues){//xValues не повторяются и упорядочены изначально, длины массивов совпадают

        this.xValues = Arrays.copyOf(xValues, xValues.length);
        this.yValues = Arrays.copyOf(yValues, xValues.length);
        count = xValues.length;
    }

    public ArrayTabulatedFunction(MathFunction source, double xFrom, double xTo, int count){//если count <1

        if (xFrom > xTo) {
            double boof = xFrom;
            xFrom = xTo;
            xTo = boof;
        }

        this.count = count;

        xValues = new double[count];
        yValues = new double[count];

        if (xFrom!=xTo){
            //double curX = xFrom;

            int UpLim = count-1;
            double step = (xTo-xFrom)/(UpLim);

            for (int i =0; i<UpLim;++i){
                xValues[i] = xFrom;
                yValues[i] = source.apply(xFrom);
                xFrom+=step;
            }

            xValues[UpLim] = xTo;
            yValues[UpLim] = source.apply(xTo);
        }

       else {
            for (int i =0; i<count;++i){
                xValues[i] = xFrom;
                yValues[i] = source.apply(xFrom);
            }
        }

    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public double getX(int index) {
        if (index >= count || index < 0) return -1.0;
        return xValues[index];
    }

    @Override
    public double getY(int index) {
        if (index >= count || index < 0) return -1.0;
        return yValues[index];
    }

    @Override
    public void setY(int index, double value) {
        if (index >= count || index < 0) return;//имеется ошибочка
        yValues[index] = value;
    }

    @Override
    public double leftBound() {
        return xValues[0];
    }

    @Override
    public double rightBound() {
        return xValues[count-1];
    }

    @Override
    public int indexOfX(double x) {
        for (int i=0; i<count;++i){
            if (xValues[i] == x) return i;
        }
        return -1;
    }

    @Override
    public int indexOfY(double y) {
        for (int i=0; i<count;++i){
            if (yValues[i] == y) return i;
        }
        return -1;
    }

    @Override
    protected int floorIndexOfX(double x) {

        for (int i=0; i<count;++i){
            if (xValues[i] == x) return i;
        }

        int ind = 0;
        double max = xValues[0];

        for (int i=0; i<count;++i) {
            if (max < xValues[i] && xValues[i] < x) {
                max = xValues[i];
                ind = i;
            }
        }
        return ind;
    }

    @Override
    protected double interpolate(double x, int floorIndex) {
        double leftX = getX(floorIndex);
        double leftY = getY(floorIndex);
        double rightX = getX(floorIndex+1);
        double rightY = getY(floorIndex+1);

        return interpolate(x, leftX, rightX, leftY, rightY);
    }

    protected double clever_interpolate(double x, int floorIndex) {//индекс левого икса
        if (count == 1) return yValues[0];

        double leftX = xValues[floorIndex];
        double leftY = yValues[floorIndex];
        if(x==rightBound()||x==leftBound()) return yValues[indexOfX(x)];

        if (x < rightBound() && x>leftBound()){

            double rightX = xValues[floorIndex+1];
            double rightY = yValues[floorIndex+1];

            return(interpolate(x, leftX, rightX,leftY,rightY));
        }

        else if (x>rightBound()){
            int UpLim = count-1;
            double step = (rightBound()-leftBound())/(UpLim);

            double newX;
            double newY;

            while (x > rightBound()){

                newX = rightBound()+step;
                newY = extrapolateRight(newX);

                var xValuesNew = Arrays.copyOfRange(xValues, 0, xValues.length+1);
                xValuesNew[count] = newX;
                var yValuesNew = Arrays.copyOfRange(yValues, 0, yValues.length+1);
                yValuesNew[count] = newY;

                xValues = new double[count+1];
                yValues = new double[count+1];
                xValues = xValuesNew;
                yValues = yValuesNew;

                count+=1;
            }
            return interpolate(x, floorIndexOfX(x));
        }

        else{
            int UpLim = count-1;
            double step = (rightBound()-leftBound())/(UpLim);

            double newX;
            double newY;

            while (x < leftBound()){

                newX = leftBound()-step;
                newY = extrapolateLeft(newX);

                var xValuesNew = Arrays.copyOfRange(xValues, 0, xValues.length+1);

                double tmp;
                for (int i = 0; i < xValues.length; i++) {
                    tmp = xValuesNew[i + 1];
                    xValuesNew[i + 1] = xValuesNew[0];
                    xValuesNew[0] = tmp;
                }
                xValuesNew[0] = newX;

                var yValuesNew = Arrays.copyOfRange(yValues, 0, yValues.length+1);
                for (int i = 0; i < yValues.length; i++) {
                    tmp = yValuesNew[i + 1];
                    yValuesNew[i + 1] = yValuesNew[0];
                    yValuesNew[0] = tmp;
                }
                yValuesNew[0] = newY;

                xValues = new double[count+1];
                yValues = new double[count+1];
                xValues = xValuesNew;
                yValues = yValuesNew;
                count+=1;
            }

            return interpolate(x, floorIndexOfX(x));
        }
    }

    public double clever_interpolate(double x){
        return clever_interpolate(x, floorIndexOfX(x));
    }

    public double interpolate(double x){
        return interpolate(x, floorIndexOfX(x));
    }

    @Override
    double interpolate(double x, double leftX, double rightX, double leftY, double rightY) {
        if (count == 1) return yValues[0];
        return super.interpolate(x, leftX, rightX, leftY, rightY);
    }

    @Override
    protected double extrapolateLeft(double x) {
        if (count == 1) return yValues[0];
        double leftX = leftBound();
        double leftY = getY(indexOfX(leftX));

        double rightX = getX(indexOfX(leftX)+1);
        double rightY = getY(indexOfX(rightX));

        return leftY + (x - leftX)/(rightX- leftX)*(rightY- leftY);
    }

    @Override
    protected double extrapolateRight(double newX) {

        if (count == 1) return yValues[0];
        double x = rightBound();
        double y = getY(indexOfX(x));

        double lowerX = getX(indexOfX(x)-1);
        double lowerY = getY(indexOfX(lowerX));

        return lowerY + (newX - lowerX)/(x-lowerX)*(y-lowerY);
    }
}
