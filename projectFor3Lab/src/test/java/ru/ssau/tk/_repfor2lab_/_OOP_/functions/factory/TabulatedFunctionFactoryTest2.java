package ru.ssau.tk._repfor2lab_._OOP_.functions.factory;

import org.junit.jupiter.api.Test;
import ru.ssau.tk._repfor2lab_._OOP_.functions.StrictTabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.TabulatedFunction;

import static org.junit.jupiter.api.Assertions.*;

class TabulatedFunctionFactoryTest2 {

    @Test
    void testCreateStrictWithDifferentFactories() {
        // Тестируем с ArrayTabulatedFunctionFactory
        TabulatedFunctionFactory arrayFactory = new ArrayTabulatedFunctionFactory();
        testFactoryStrictBehavior(arrayFactory, "ArrayTabulatedFunctionFactory");

        // Тестируем с LinkedListTabulatedFunctionFactory
        TabulatedFunctionFactory linkedListFactory = new LinkedListTabulatedFunctionFactory();
        testFactoryStrictBehavior(linkedListFactory, "LinkedListTabulatedFunctionFactory");
    }

    private void testFactoryStrictBehavior(TabulatedFunctionFactory factory, String factoryName) {
        double[] xValues = {0.0, 1.0, 2.0};
        double[] yValues = {10.0, 20.0, 30.0};

        TabulatedFunction strictFunction = factory.createStrict(xValues, yValues);

        // Проверяем тип
        assertInstanceOf(StrictTabulatedFunction.class, strictFunction);

        // Проверяем корректность значений в узлах
        assertEquals(10.0, strictFunction.apply(0.0));
        assertEquals(20.0, strictFunction.apply(1.0));
        assertEquals(30.0, strictFunction.apply(2.0));

        // Проверяем строгость
        assertThrows(UnsupportedOperationException.class, () -> strictFunction.apply(0.5));
        assertThrows(UnsupportedOperationException.class, () -> strictFunction.apply(1.5));
    }
}
