package ru.ssau.tk._repfor2lab_._OOP_.functions;

import ru.ssau.tk._repfor2lab_._OOP_.exceptions.InterpolationException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ArrayTabulatedFunction extends AbstractTabulatedFunction implements Serializable, Insertable, Removable {

    private static final long serialVersionUID = -7260590353279522614L;
    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    private double[] xValues;

    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    private double[] yValues;


    public Iterator<Point> iterator(){
        return new Iterator<Point>() {

            private int i =0;

            @Override
            public boolean hasNext() {
                return i<count;
            }

            @Override
            public Point next() {
                if (!hasNext()){
                    throw new NoSuchElementException("Элементов не осталось");}
                Point point = new Point(xValues[i], yValues[i]);
                ++i;
                return point;
            }
        };
    }

    private int count;

    @Override
    public void insert(double x, double y) {
        for (int i = 0; i < count; i++) { //поиск х
            if (xValues[i] == x) {
                yValues[i] = y; //замена значения
                return;
            }
        }
        int insertIndex = 0; //поиск позиции для вставки
        while (insertIndex < count && xValues[insertIndex] < x) {
            insertIndex++;
        }
        double[] newXValues = new double[count + 1]; //создание увеличенных массивов
        double[] newYValues = new double[count + 1];

        System.arraycopy(xValues, 0, newXValues, 0, insertIndex); //копирование эл-тов до позиции вставки
        System.arraycopy(yValues, 0, newYValues, 0, insertIndex);

        newXValues[insertIndex] = x; //вставка нового элемента
        newYValues[insertIndex] = y;

        System.arraycopy(xValues, insertIndex, newXValues, insertIndex + 1, count - insertIndex); //копирование эл-тов после позиции вставки
        System.arraycopy(yValues, insertIndex, newYValues, insertIndex + 1, count - insertIndex);

        xValues = newXValues; //замена старых массивов
        yValues = newYValues;
        count++;
    }

    @Override
    public void remove(int index) {

        if (count == 0 || index <0 || index >= count) throw new IllegalArgumentException("Неверный индекс для удаления");

        //новые массивы уменьшенного размера
        double[] newXArray = new double[count - 1];
        double[] newYArray = new double[count - 1];

        //копирует левую часть до удаляемого элемента
        System.arraycopy(xValues, 0, newXArray, 0, index);
        System.arraycopy(yValues, 0, newYArray, 0, index);

        //копирует правую часть до удаляемого элемента
        System.arraycopy(xValues, index + 1, newXArray, index, count - index - 1);
        System.arraycopy(yValues, index + 1, newYArray, index, count - index - 1);
        this.xValues = newXArray; //заменяет старые массивы на новые
        this.yValues = newYArray;
        this.count--;
    }

    public ArrayTabulatedFunction(double[] xValues, double[] yValues){//xValues не повторяются и упорядочены изначально, длины массивов совпадают
        if (xValues.length < 2 || yValues.length < 2) {
            throw new IllegalArgumentException("Длина массивов меньше минимальной возможной");
        }
        checkLengthIsTheSame(xValues, yValues);
        checkSorted(xValues);
        this.xValues = Arrays.copyOf(xValues, xValues.length);
        this.yValues = Arrays.copyOf(yValues, xValues.length);
        count = xValues.length;
    }

    public ArrayTabulatedFunction(MathFunction source, double xFrom, double xTo, int count){//если count <1
        if (count < 2) throw new IllegalArgumentException("Количество элементов меньше минимума");
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
        if (index == count && count == 0) throw new NullPointerException("Обращение к пустому массиву");
        else if (index >= count || index < 0) throw new IllegalArgumentException("Индекс выходит за пределы");
        return xValues[index];
    }

    @Override
    public double getY(int index) {
        if (index == count && count == 0) throw new NullPointerException("Обращение к пустому массиву");
        else if (index >= count || index < 0) throw new IllegalArgumentException("Индекс выходит за пределы");
        return yValues[index];
    }

    @Override
    public void setY(int index, double value) {
        if (index == count && count == 0) throw new NullPointerException("Обращение к пустому массиву");
        else if (index >= count || index < 0) throw new IllegalArgumentException("Индекс выходит за пределы");
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

        if (x < xValues[0]) throw new IllegalArgumentException("Икс меньше левой границы");
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
        if (count <= 1) throw new InterpolationException("Мало аргументов для интерполяции");
        return interpolate(x, floorIndexOfX(x));
    }

    @Override
    protected double interpolate(double x, int floorIndex) {
        if (count <= 1) throw new InterpolationException("Мало аргументов для интерполяции");
        double leftX = getX(floorIndex);
        double leftY = getY(floorIndex);
        double rightX = getX(floorIndex+1);
        double rightY = getY(floorIndex+1);

        return interpolate(x, leftX, rightX, leftY, rightY);
    }

    @Override
    protected double interpolate(double x, double leftX, double rightX, double leftY, double rightY) {
        return super.interpolate(x, leftX, rightX, leftY, rightY);
    }

    @Override
    protected double extrapolateLeft(double x) {
        if (count <= 1) throw new InterpolationException("Мало аргументов для экстраполяции");
        double leftX = leftBound();
        double leftY = getY(indexOfX(leftX));

        double rightX = getX(indexOfX(leftX)+1);
        double rightY = getY(indexOfX(rightX));

        return (leftY*(rightX-x)-rightY*(leftX-x))/(rightX-leftX);
    }

    @Override
    protected double extrapolateRight(double newX) {
        if (count <= 1) throw new InterpolationException("Мало аргументов для экстраполяции");
        double x = rightBound();
        double y = getY(indexOfX(x));

        double lowerX = getX(indexOfX(x)-1);
        double lowerY = getY(indexOfX(lowerX));

        return lowerY + (newX - lowerX)/(x-lowerX)*(y-lowerY);
    }
}
