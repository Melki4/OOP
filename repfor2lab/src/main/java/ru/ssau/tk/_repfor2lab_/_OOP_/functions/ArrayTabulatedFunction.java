package ru.ssau.tk._repfor2lab_._OOP_.functions;

import java.util.Arrays;

public class ArrayTabulatedFunction extends AbstractTabulatedFunction{

    private double[] xValues;
    private double[] yValues;
    private int count;

    @Override
    public void insert(double x, double y) {
        if (count == 0){
            xValues = new double[1];
            yValues = new double[1];

            xValues[0] = x;
            yValues[0] = y;

            count++;
            return;
        }
        else if (indexOfX(x)!= -1) {
            setY(indexOfX(x), y);
            return;
        }
        else if (x < leftBound()){
             var xValuesNew = Arrays.copyOfRange(xValues, 0, xValues.length+1);
             var yValuesNew = Arrays.copyOfRange(yValues, 0, yValues.length+1);

             double tmp;
             for (int i = 0; i < xValues.length; i++) {
                 tmp = xValuesNew[i + 1];
                 xValuesNew[i + 1] = xValuesNew[0];
                 xValuesNew[0] = tmp;

                 tmp = yValuesNew[i + 1];
                 yValuesNew[i + 1] = yValuesNew[0];
                 yValuesNew[0] = tmp;
             }

             xValuesNew[0] = x;
             yValuesNew[0] = y;

             xValues = xValuesNew;
             yValues = yValuesNew;
             count+=1;
             return;
        }
        else if (x > rightBound()){
            var xValuesNew = Arrays.copyOfRange(xValues, 0, xValues.length+1);
            var yValuesNew = Arrays.copyOfRange(yValues, 0, yValues.length+1);

            xValuesNew[xValues.length] = x;
            yValuesNew[xValues.length] = y;

            xValues = xValuesNew;
            yValues = yValuesNew;
            count+=1;
            return;
        }
        else {
            int index = floorIndexOfX(x);

            var leftPartX = Arrays.copyOfRange(xValues, 0, index+1);
            var rightPartX = Arrays.copyOfRange(xValues, index+1, xValues.length);

            var leftPartY = Arrays.copyOfRange(yValues, 0, index+1);
            var rightPartY = Arrays.copyOfRange(yValues, index+1, xValues.length);

            xValues = new double[count+1];
            yValues = new double[count+1];

            for(int i=0;i<index+1;++i){
                xValues[i] = leftPartX[i];
                yValues[i] = leftPartY[i];
            }

            xValues[index+1] = x;
            yValues[index+1] = y;

            for(int i=index+2;i<count+1;++i){
                xValues[i] = rightPartX[i-(index+2)];
                yValues[i] = rightPartY[i-(index+2)];
            }

            count+=1;
            return;
        }
    }

    @Override
    public void remove(int indexDelX) {
        if (count == 0 || indexDelX <0 || indexDelX >= count){
            return;
        }
        else if (count == 1) {
            xValues = new double[0];
            yValues = new double[0];
            count = 0;
        }
        else if (indexDelX == 0){
            xValues = Arrays.copyOfRange(xValues, 1, count);
            yValues = Arrays.copyOfRange(yValues, 1, count);
            count--;
        }
        else if (indexDelX == count-1){
            xValues = Arrays.copyOfRange(xValues, 0, count-1);
            yValues = Arrays.copyOfRange(yValues, 0, count-1);
            count--;
        }
        else {
            int index = indexDelX-1;

            var leftPartX = Arrays.copyOfRange(xValues, 0, index+1);
            var rightPartX = Arrays.copyOfRange(xValues, index+2, xValues.length);

            var leftPartY = Arrays.copyOfRange(yValues, 0, index+1);
            var rightPartY = Arrays.copyOfRange(yValues, index+2, xValues.length);

            xValues = new double[count-1];
            yValues = new double[count-1];

            for(int i=0;i<index+1;++i){
                xValues[i] = leftPartX[i];
                yValues[i] = leftPartY[i];
            }

            for(int i=index+1;i<count-1;++i){
                xValues[i] = rightPartX[i-(index+1)];
                yValues[i] = rightPartY[i-(index+1)];
            }

            count--;
            return;
        }
    }

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

    protected double interpolate(double x){
        return interpolate(x, floorIndexOfX(x));
    }

    @Override
    protected double interpolate(double x, int floorIndex) {
        double leftX = getX(floorIndex);
        double leftY = getY(floorIndex);
        double rightX = getX(floorIndex+1);
        double rightY = getY(floorIndex+1);

        return interpolate(x, leftX, rightX, leftY, rightY);
    }

    @Override
    protected double interpolate(double x, double leftX, double rightX, double leftY, double rightY) {
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
