package ru.ssau.tk._repfor2lab_._OOP_.concurrent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.ssau.tk._repfor2lab_._OOP_.functions.ArrayTabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.LinkedListTabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.MathFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.Point;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

class SynchronizedTabulatedFunctionTest {

    SynchronizedTabulatedFunction list;
    SynchronizedTabulatedFunction array;

    @BeforeEach
    void create(){

        double[] xValues = {1, 2, 3, 4, 5, 6, 7,8, 9, 10};
        double[] yValues = {1.2, 1.3, 1.4, 1.4, 1.6, 1.8, 1.5, 1.5, 1.6, 1.7};

        MathFunction func = (double x) -> x*x -2*x+1;

        LinkedListTabulatedFunction listTabulatedFunction = new LinkedListTabulatedFunction(xValues, yValues);
        ArrayTabulatedFunction arrayTabulatedFunction = new ArrayTabulatedFunction(func, 1, 10, 20);

        list = new SynchronizedTabulatedFunction(listTabulatedFunction);
        array = new SynchronizedTabulatedFunction(arrayTabulatedFunction);
    }

    @Test
    void getCount() {
        assertEquals(10, list.getCount());
        assertEquals(20, array.getCount());
    }

    @Test
    void getX() {
        assertEquals(3.0, list.getX(2));
        assertEquals(10.0, array.getX(19));
    }

    @Test
    void getY() {
        assertEquals(1.4, list.getY(2));
        assertEquals(81.0, array.getY(19));
    }

    @Test
    void setY() {
        list.setY(2, 101.4);
        array.setY(19, 13.4);
        assertEquals(101.4, list.getY(2));
        assertEquals(13.4, array.getY(19));
    }

    @Test
    void indexOfX() {
        assertEquals(-1, array.indexOfX(101));
        assertEquals(-1, list.indexOfX(101));
        assertEquals(0, list.indexOfX(1.0));
        assertEquals(0, array.indexOfX(1.0));
    }

    @Test
    void indexOfY() {
        assertEquals(-1, array.indexOfY(101));
        assertEquals(-1, list.indexOfY(101));
        assertEquals(2, list.indexOfY(1.4));
        assertEquals(19, array.indexOfY(81.0));
    }

    @Test
    void leftBound() {
        assertEquals(1.0, list.leftBound());
        assertEquals(1.0, array.leftBound());
    }

    @Test
    void rightBound() {
        assertEquals(10.0, list.rightBound());
        assertEquals(10.0, array.rightBound());
    }

    @Test
    void iterator() {
        synchronized (list){
            Iterator<Point> iterator = list.iterator();
            int i =0;
            Point boof;
            while(iterator.hasNext()){
                boof = iterator.next();
                assertEquals(list.getX(i), boof.x);
                assertEquals(list.getY(i), boof.y);
                i++;
            }
        }

        synchronized (array){
            Iterator<Point> iterator = array.iterator();
            int i = 0;
            Point boof;
            while(iterator.hasNext()){
                boof = iterator.next();
                assertEquals(array.getX(i), boof.x);
                assertEquals(array.getY(i), boof.y);
                i++;
            }
        }
    }
}