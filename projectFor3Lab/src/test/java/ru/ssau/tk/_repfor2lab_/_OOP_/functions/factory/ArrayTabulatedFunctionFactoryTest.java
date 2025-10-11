package ru.ssau.tk._repfor2lab_._OOP_.functions.factory;
import org.junit.jupiter.api.Test;
import ru.ssau.tk._repfor2lab_._OOP_.functions.ArrayTabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.TabulatedFunction;

import static org.junit.jupiter.api.Assertions.*;

class ArrayTabulatedFunctionFactoryTest {
    private final ArrayTabulatedFunctionFactory factory;
    private final double[] xValues;
    private final double[] yValues;

    public ArrayTabulatedFunctionFactoryTest() {
        factory = new ArrayTabulatedFunctionFactory();
        xValues = new double[]{1.0, 2.0, 3.0, 4.0, 5.0};
        yValues = new double[]{2.0, 4.0, 6.0, 8.0, 10.0};
    }

    @Test
    public void testCreateReturnsCorrectType() {
        TabulatedFunction function = factory.create(xValues, yValues);

        assertTrue(function instanceof ArrayTabulatedFunction,
                "Created function should be instance of ArrayTabulatedFunction");
    }

    @Test
    public void testCreateWithDifferentDataSizes() {
        double[] smallXValues = {1.0, 2.0};
        double[] smallYValues = {1.0, 4.0};

        double[] largeXValues = {-2.0, -1.0, 0.0, 1.0, 2.0};
        double[] largeYValues = {4.0, 1.0, 0.0, 1.0, 4.0};

        TabulatedFunction smallFunction = factory.create(smallXValues, smallYValues);
        TabulatedFunction largeFunction = factory.create(largeXValues, largeYValues);

        assertTrue(smallFunction instanceof ArrayTabulatedFunction);
        assertTrue(largeFunction instanceof ArrayTabulatedFunction);
    }

    @Test
    public void testCreatedObjectIsNotLinkedListType() {
        TabulatedFunction function = factory.create(xValues, yValues);

        // Проверяем, что созданный объект НЕ является LinkedListTabulatedFunction
        assertFalse(function instanceof ru.ssau.tk._repfor2lab_._OOP_.functions.LinkedListTabulatedFunction,
                "Array factory should not create LinkedList objects");
    }

    @Test
    public void testCreateFunctionality() {
        TabulatedFunction function = factory.create(xValues, yValues);

        assertEquals(5, function.getCount());
        assertEquals(1.0, function.getX(0), 0.0001);
        assertEquals(2.0, function.getY(0), 0.0001);
        assertEquals(5.0, function.getX(4), 0.0001);
        assertEquals(10.0, function.getY(4), 0.0001);
    }

    @Test
    public void testCreateWithSingleElement() {
        double[] singleX = {1.0};
        double[] singleY = {2.0};

        assertThrows(IllegalArgumentException.class, () -> {
            factory.create(singleX, singleY);
        });
    }
}