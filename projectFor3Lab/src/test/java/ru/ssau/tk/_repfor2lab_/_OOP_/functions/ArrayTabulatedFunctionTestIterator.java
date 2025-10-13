package ru.ssau.tk._repfor2lab_._OOP_.functions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class ArrayTabulatedFunctionTestIterator {
    @Test
    public void testIteratorWithWhileLoop() {

        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {-13.3, 232.0, 22.0, -3233.3};

        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        Iterator<Point> iterator = function.iterator();
        int index = 0;

        while (iterator.hasNext()) {
            Point point = iterator.next();
            assertEquals(point.x, xValues[index]);
            assertEquals(point.y, yValues[index]);
            index++;
        }

        assertEquals(index, xValues.length, "Нужно перебрать все элементы");
    }
    @Test
    public void testIteratorWithForEachLoop() {

        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {1.3, 4.2, 91.4, 1323.3};

        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        int index = 0;
        for (var point : function) {
            assertEquals(point.x, xValues[index]);
            assertEquals(point.y, yValues[index]);
            index++;
        }

        assertEquals(index, xValues.length, "Нужно перебрать все элементы");
    }
    @Test
    public void testIteratorWithSingleElement() {

        double[] xValuesTemp = {1.0, 2.0};
        double[] yValuesTemp = {1.0, 4.0};

        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValuesTemp, yValuesTemp);
        function.remove(1);

        Iterator<Point> iterator = function.iterator();

        assertTrue(iterator.hasNext());
        Point point = iterator.next();
        assertEquals(1.0, point.x);
        assertEquals(1.0, point.y);

        assertFalse(iterator.hasNext());
    }
    @Test
    public void testIteratorWithError() {

        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {-13.3, 232.0, 22.0, -3233.3};

        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        Iterator<Point> iterator = function.iterator();
        int index = 0;

        while (index < 4) {
            Point point = iterator.next();
            assertEquals(point.x, xValues[index]);
            assertEquals(point.y, yValues[index]);
            index++;
        }

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            Point point = iterator.next();
        });
        Assertions.assertEquals("Элементов не осталось", exception.getMessage());
    }
}