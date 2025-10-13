package ru.ssau.tk._repfor2lab_._OOP_.functions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.ssau.tk._repfor2lab_._OOP_.exceptions.DifferentLengthOfArraysException;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

class StrictTabulatedFunctionTest {
    @Test
    void getCount() {
        double[] xValues1 = {1.0, 2.0, 3.0};
        double[] yValues1 = {1.0, 4.0, 9.0};
        TabulatedFunction arrayFunc = new ArrayTabulatedFunction(xValues1, yValues1);
        StrictTabulatedFunction strictArrayFunc = new StrictTabulatedFunction(arrayFunc);
        assertEquals(3, strictArrayFunc.getCount(), "Array implementation should have 3 points");
    }

    @Test
    void getX() {
        double[] xValues1 = {1.0, 2.2, 3.0};
        double[] yValues1 = {1.0, 4.0, 9.0};
        TabulatedFunction arrayFunc = new ArrayTabulatedFunction(xValues1, yValues1);
        StrictTabulatedFunction strictArrayFunc = new StrictTabulatedFunction(arrayFunc);
        assertEquals(2.2, strictArrayFunc.getX(1));
    }

    @Test
    void getY() {
        double[] xValues1 = {1.0, 2.0, 3.0};
        double[] yValues1 = {1.0, 4.0, 9.4};
        TabulatedFunction arrayFunc = new ArrayTabulatedFunction(xValues1, yValues1);
        StrictTabulatedFunction strictArrayFunc = new StrictTabulatedFunction(arrayFunc);
        assertEquals(9.4, strictArrayFunc.getY(2));
    }

    @Test
    void setY() {
        double[] xValues1 = {1.0, 2.0, 3.0};
        double[] yValues1 = {1.0, 4.0, 9.4};
        TabulatedFunction arrayFunc = new ArrayTabulatedFunction(xValues1, yValues1);
        StrictTabulatedFunction strictArrayFunc = new StrictTabulatedFunction(arrayFunc);
        strictArrayFunc.setY(2, 7);
        assertEquals(7.0, strictArrayFunc.getY(2));
    }

    @Test
    void indexOfX() {
        double[] xValues1 = {1.0, 2.0, 3.0};
        double[] yValues1 = {1.0, 4.0, 9.4};
        TabulatedFunction arrayFunc = new ArrayTabulatedFunction(xValues1, yValues1);
        StrictTabulatedFunction strictArrayFunc = new StrictTabulatedFunction(arrayFunc);
        assertEquals(-1, strictArrayFunc.indexOfX(4));
    }

    @Test
    void indexOfY() {
        double[] xValues1 = {1.0, 2.0, 3.0};
        double[] yValues1 = {1.0, 4.0, 9.4};
        TabulatedFunction arrayFunc = new ArrayTabulatedFunction(xValues1, yValues1);
        StrictTabulatedFunction strictArrayFunc = new StrictTabulatedFunction(arrayFunc);
        assertEquals(1, strictArrayFunc.indexOfY(4.0));
    }

    @Test
    void leftBound() {
        double[] xValues1 = {1.1, 2.0, 3.0};
        double[] yValues1 = {1.0, 4.0, 9.4};
        TabulatedFunction arrayFunc = new ArrayTabulatedFunction(xValues1, yValues1);
        StrictTabulatedFunction strictArrayFunc = new StrictTabulatedFunction(arrayFunc);
        assertEquals(1.1, strictArrayFunc.leftBound());
    }

    @Test
    void rightBound() {
        double[] xValues1 = {1.1, 2.0, 3.3};
        double[] yValues1 = {1.0, 4.0, 9.4};
        TabulatedFunction arrayFunc = new ArrayTabulatedFunction(xValues1, yValues1);
        StrictTabulatedFunction strictArrayFunc = new StrictTabulatedFunction(arrayFunc);
        assertEquals(3.3, strictArrayFunc.rightBound());
    }

    @Test
    void iterator1() {
        double[] xValuesTemp = {1.0, 2.0};
        double[] yValuesTemp = {1.0, 4.0};

        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValuesTemp, yValuesTemp);
        StrictTabulatedFunction strictArrayFunc = new StrictTabulatedFunction(function );

        Iterator<Point> iterator = strictArrayFunc.iterator();

        assertTrue(iterator.hasNext());
        Point point = iterator.next();
        assertEquals(1.0, point.x);
        assertEquals(1.0, point.y);
        iterator.next();
        assertFalse(iterator.hasNext());
    }

    void iterator2()  {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {1.0, 4.0, 9.0, 16.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);
        StrictTabulatedFunction strictArrayFunc = new StrictTabulatedFunction(function );

        Iterator<Point> iterator = strictArrayFunc.iterator();
        int index = 0;

        while (iterator.hasNext()) {
            Point point = iterator.next();
            assertEquals(point.x, xValues[index], 0.0001);
            assertEquals(point.y, yValues[index], 0.0001);
            index++;
        }

        assertEquals(index, xValues.length, "Нужно перебрать все элементы");
    }


    @Test
    void apply() {
        double[] xValues = {0.00, 0.20, 0.40, 0.60, 0.80};
        double[] yValues = {1.00, 1.179, 1.310, 1.390, 1.414};

        var TestedVar = new ArrayTabulatedFunction(xValues, yValues);

        StrictTabulatedFunction strictArrayFunc = new StrictTabulatedFunction(TestedVar);

        Exception exception = assertThrows(UnsupportedOperationException.class, () -> {
            strictArrayFunc.apply(0.77);
        });
        Assertions.assertEquals("Запрещено интерполирование", exception.getMessage());
        assertEquals(1.179, strictArrayFunc.apply(0.2), 0.1);
    }

}