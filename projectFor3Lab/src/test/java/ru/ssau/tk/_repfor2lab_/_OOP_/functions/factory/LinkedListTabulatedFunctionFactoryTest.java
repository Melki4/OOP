package ru.ssau.tk._repfor2lab_._OOP_.functions.factory;
import org.junit.jupiter.api.Test;
import ru.ssau.tk._repfor2lab_._OOP_.functions.LinkedListTabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.TabulatedFunction;

import static org.junit.jupiter.api.Assertions.*;

class LinkedListTabulatedFunctionFactoryTest {
    private final LinkedListTabulatedFunctionFactory factory;
    private final double[] xValues;
    private final double[] yValues;

    public LinkedListTabulatedFunctionFactoryTest() {
        factory = new LinkedListTabulatedFunctionFactory();
        xValues = new double[]{1.0, 2.0, 3.0, 4.0, 5.0};
        yValues = new double[]{2.0, 4.0, 6.0, 8.0, 10.0};
    }

    @Test
    public void testCreateReturnsCorrectType() {
        TabulatedFunction function = factory.create(xValues, yValues);

        assertTrue(function instanceof LinkedListTabulatedFunction,
                "Created function should be instance of LinkedListTabulatedFunction");
    }

    @Test
    public void testCreateWithDifferentDataSizes() {
        double[] smallXValues = {1.0, 2.0};
        double[] smallYValues = {1.0, 4.0};

        double[] largeXValues = {-2.0, -1.0, 0.0, 1.0, 2.0};
        double[] largeYValues = {4.0, 1.0, 0.0, 1.0, 4.0};

        TabulatedFunction smallFunction = factory.create(smallXValues, smallYValues);
        TabulatedFunction largeFunction = factory.create(largeXValues, largeYValues);

        assertTrue(smallFunction instanceof LinkedListTabulatedFunction);
        assertTrue(largeFunction instanceof LinkedListTabulatedFunction);
    }

    @Test
    public void testCreatedObjectIsNotArrayType() {
        TabulatedFunction function = factory.create(xValues, yValues);

        assertFalse(function instanceof ru.ssau.tk._repfor2lab_._OOP_.functions.ArrayTabulatedFunction,
                "LinkedList factory should not create Array objects");
    }
}