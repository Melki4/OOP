package ru.ssau.tk._repfor2lab_._OOP_.concurrent;

import ru.ssau.tk._repfor2lab_._OOP_.functions.Point;
import ru.ssau.tk._repfor2lab_._OOP_.functions.TabulatedFunction;

import java.util.Iterator;

public class SynchronizedTabulatedFunction implements TabulatedFunction {

    final TabulatedFunction function;

    public interface Operation<T>{
        T apply(SynchronizedTabulatedFunction f);
    }

    <T> T doSynchronously(Operation<? extends T> operation){
        return operation.apply(this);
    }

//    Collection<Integer> syncCollection = Collections.synchronizedCollection(new ArrayList<>());

    public SynchronizedTabulatedFunction(TabulatedFunction f){
        function = f;
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
    public Iterator<Point> iterator() {return function.iterator(); }

    @Override
    public double apply(double x) { synchronized (function) {return function.apply(x);} }
}
