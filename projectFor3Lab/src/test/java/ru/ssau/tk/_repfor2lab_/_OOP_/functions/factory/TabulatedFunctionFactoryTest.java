package ru.ssau.tk._repfor2lab_._OOP_.functions.factory;

import org.junit.jupiter.api.Test;
import ru.ssau.tk._repfor2lab_._OOP_.exceptions.DifferentLengthOfArraysException;
import ru.ssau.tk._repfor2lab_._OOP_.functions.StrictTabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.TabulatedFunction;

import static org.junit.jupiter.api.Assertions.*;

class TabulatedFunctionFactoryTest {

    @Test
    void testCreateStrict() {
        TabulatedFunctionFactory factory = new ArrayTabulatedFunctionFactory();

        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {2.0, 4.0, 6.0, 8.0};

        TabulatedFunction strictFunction = factory.createStrict(xValues, yValues);

        // Проверяем, что возвращается StrictTabulatedFunction
        assertInstanceOf(StrictTabulatedFunction.class, strictFunction);

        // Проверяем, что значения корректны
        assertEquals(2.0, strictFunction.apply(1.0));
        assertEquals(4.0, strictFunction.apply(2.0));
        assertEquals(6.0, strictFunction.apply(3.0));
        assertEquals(8.0, strictFunction.apply(4.0));

        // Проверяем строгость - вне узлов должно бросаться исключение
        assertThrows(UnsupportedOperationException.class, () -> strictFunction.apply(1.5));
        assertThrows(UnsupportedOperationException.class, () -> strictFunction.apply(2.7));
        assertThrows(UnsupportedOperationException.class, () -> strictFunction.apply(3.3));
    }

    @Test
    void testCreateStrictWithSinglePoint() {

        TabulatedFunctionFactory factory = new ArrayTabulatedFunctionFactory();

        double[] xValues = {5.0, 5.6};
        double[] yValues = {10.0, 18.1};

        TabulatedFunction strictFunction = factory.createStrict(xValues, yValues);

        assertInstanceOf(StrictTabulatedFunction.class, strictFunction);
        assertEquals(10.0, strictFunction.apply(5.0));

        // Должно бросать исключение
        assertThrows(UnsupportedOperationException.class, () -> strictFunction.apply(4.9));
        assertThrows(UnsupportedOperationException.class, () -> strictFunction.apply(5.1));
    }

    @Test
    void testCreateStrictWithEmptyArrays() {
        TabulatedFunctionFactory factory = new ArrayTabulatedFunctionFactory();

        double[] xValues = {};
        double[] yValues = {};

        // Должно бросать исключение при создании с пустыми массивами
        assertThrows(IllegalArgumentException.class, () -> factory.createStrict(xValues, yValues));
    }

    @Test
    void testCreateStrictWithDifferentLengthArrays() {
        TabulatedFunctionFactory factory = new ArrayTabulatedFunctionFactory();

        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 2.0}; // разная длина

        // Должно бросать исключение при разных длинах массивов
        assertThrows(DifferentLengthOfArraysException.class, () -> factory.createStrict(xValues, yValues));
    }
}

