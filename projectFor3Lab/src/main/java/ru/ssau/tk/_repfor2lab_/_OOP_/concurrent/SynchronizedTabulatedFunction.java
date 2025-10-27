package ru.ssau.tk._repfor2lab_._OOP_.concurrent;

import ru.ssau.tk._repfor2lab_._OOP_.functions.Point;
import ru.ssau.tk._repfor2lab_._OOP_.functions.TabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.operations.TabulatedFunctionOperationService;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class SynchronizedTabulatedFunction implements TabulatedFunction {

    public SynchronizedTabulatedFunction(TabulatedFunction f){
        function = f;
    }

    final TabulatedFunction function;

    public interface Operation<T>{
        T apply(SynchronizedTabulatedFunction f);
    }

    public <T> T doSynchronously(Operation<? extends T> operation){
        synchronized (operation){
            return operation.apply(this);
        }
    }

    @Override
    public int getCount() { synchronized (function) {return function.getCount();} }

    @Override
    public double getX(int index) {synchronized (function) {return function.getX(index);} }

    @Override
    public double getY(int index) { synchronized (function) {return function.getY(index);} }

    @Override
    public void setY(int index, double value) { synchronized (function) {function.setY(index, value);} }

    @Override
    public int indexOfX(double x) { synchronized (function) {return function.indexOfX(x);} }

    @Override
    public int indexOfY(double y) { synchronized (function) {return function.indexOfY(y);} }

    @Override
    public double leftBound() {synchronized (function) {return function.leftBound();} }

    @Override
    public double rightBound() { synchronized (function) {return function.rightBound();} }

    @Override
    public Iterator<Point> iterator() {
        Point[] points;
        synchronized (function) {
            points = TabulatedFunctionOperationService.asPoints(function);
        }
        return new Iterator<Point>() {
            private int curIndex = 0;
            @Override
            public boolean hasNext() {
                return curIndex < points.length;
            }

            @Override
            public Point next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("Элементов больше нет");
                }
                return points[curIndex++];
            }
        };
    }

    @Override
    public double apply(double x) { synchronized (function) {return function.apply(x);} }
}
