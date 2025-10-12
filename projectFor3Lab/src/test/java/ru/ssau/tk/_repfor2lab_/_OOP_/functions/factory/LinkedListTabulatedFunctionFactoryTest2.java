package ru.ssau.tk._repfor2lab_._OOP_.functions.factory;

import org.junit.jupiter.api.Test;
import ru.ssau.tk._repfor2lab_._OOP_.exceptions.ArrayIsNotSortedException;
import ru.ssau.tk._repfor2lab_._OOP_.functions.StrictTabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.TabulatedFunction;

import static org.junit.jupiter.api.Assertions.*;

class LinkedListTabulatedFunctionFactoryTest2 {
    @Test
    void testCreateStrict() {
        TabulatedFunctionFactory factory = new LinkedListTabulatedFunctionFactory();

        double[] xValues = {0.0, 1.0, 2.0, 3.0};
        double[] yValues = {0.0, 1.0, 4.0, 9.0};

        TabulatedFunction strictFunction = factory.createStrict(xValues, yValues);

        // Проверяем, что возвращается StrictTabulatedFunction
        assertInstanceOf(StrictTabulatedFunction.class, strictFunction);

        // Проверяем, что значения корректны
        assertEquals(0.0, strictFunction.apply(0.0));
        assertEquals(1.0, strictFunction.apply(1.0));
        assertEquals(4.0, strictFunction.apply(2.0));
        assertEquals(9.0, strictFunction.apply(3.0));

        // Проверяем строгость - вне узлов должно бросаться исключение
        assertThrows(UnsupportedOperationException.class, () -> strictFunction.apply(0.5));
        assertThrows(UnsupportedOperationException.class, () -> strictFunction.apply(1.2));
        assertThrows(UnsupportedOperationException.class, () -> strictFunction.apply(2.8));
    }

    @Test
    void testCreateStrictWithTwoPoints() {
        TabulatedFunctionFactory factory = new LinkedListTabulatedFunctionFactory();

        double[] xValues = {-1.0, 1.0};
        double[] yValues = {1.0, 1.0};

        TabulatedFunction strictFunction = factory.createStrict(xValues, yValues);

        assertInstanceOf(StrictTabulatedFunction.class, strictFunction);
        assertEquals(1.0, strictFunction.apply(-1.0));
        assertEquals(1.0, strictFunction.apply(1.0));

        // Между точками должно бросать исключение
        assertThrows(UnsupportedOperationException.class, () -> strictFunction.apply(0.0));
        assertThrows(UnsupportedOperationException.class, () -> strictFunction.apply(-0.5));
        assertThrows(UnsupportedOperationException.class, () -> strictFunction.apply(0.5));
    }

    @Test
    void testCreateStrictWithNegativeValues() {
        TabulatedFunctionFactory factory = new LinkedListTabulatedFunctionFactory();

        double[] xValues = {-3.0, -2.0, -1.0, 0.0};
        double[] yValues = {9.0, 4.0, 1.0, 0.0};

        TabulatedFunction strictFunction = factory.createStrict(xValues, yValues);

        assertInstanceOf(StrictTabulatedFunction.class, strictFunction);

        // Проверяем отрицательные значения
        assertEquals(9.0, strictFunction.apply(-3.0));
        assertEquals(4.0, strictFunction.apply(-2.0));
        assertEquals(1.0, strictFunction.apply(-1.0));
        assertEquals(0.0, strictFunction.apply(0.0));

        // Проверяем строгость для отрицательных интервалов
        assertThrows(UnsupportedOperationException.class, () -> strictFunction.apply(-2.5));
        assertThrows(UnsupportedOperationException.class, () -> strictFunction.apply(-1.7));
    }

    @Test
    void testCreateStrictWithDuplicateXValues() {
        TabulatedFunctionFactory factory = new LinkedListTabulatedFunctionFactory();

        double[] xValues = {1.0, 2.0, 2.0, 3.0}; // дублирующиеся X
        double[] yValues = {1.0, 2.0, 3.0, 4.0};

        // Должно бросать исключение при дублирующихся X
        assertThrows(ArrayIsNotSortedException.class, () -> factory.createStrict(xValues, yValues));
    }
}
