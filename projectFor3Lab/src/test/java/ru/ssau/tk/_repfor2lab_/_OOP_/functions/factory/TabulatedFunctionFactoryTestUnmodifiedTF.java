package ru.ssau.tk._repfor2lab_._OOP_.functions.factory;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import ru.ssau.tk._repfor2lab_._OOP_.functions.TabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.UnmodifiableTabulatedFunction;

class TabulatedFunctionFactoryTestUnmodifiedTF {
    // Базовая функциональность
    @Test
    void testCreateUnmodifiableBasicFunctionality() {
        TabulatedFunctionFactory factory = new ArrayTabulatedFunctionFactory();

        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {2.0, 4.0, 6.0, 8.0};

        TabulatedFunction unmodifiableFunction = factory.createUnmodifiable(xValues, yValues);

        // Проверяем, что возвращается UnmodifiableTabulatedFunction
        assertInstanceOf(UnmodifiableTabulatedFunction.class, unmodifiableFunction);

        // Проверяем, что значения можно читать
        assertEquals(2.0, unmodifiableFunction.apply(1.0));
        assertEquals(4.0, unmodifiableFunction.apply(2.0));
        assertEquals(6.0, unmodifiableFunction.apply(3.0));
        assertEquals(8.0, unmodifiableFunction.apply(4.0));

        // Проверяем интерполяцию (должна работать для немодифицируемой функции)
        assertEquals(3.0, unmodifiableFunction.apply(1.5), 0.0001);
        assertEquals(5.0, unmodifiableFunction.apply(2.5), 0.0001);
    }

    // Проверка немодифицируемости - setY недоступен
    @Test
    void testCreateUnmodifiableSetYThrowsException() {
        TabulatedFunctionFactory factory = new ArrayTabulatedFunctionFactory();

        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};

        TabulatedFunction unmodifiableFunction = factory.createUnmodifiable(xValues, yValues);

        // Проверяем, что setY бросает UnsupportedOperationException
        assertThrows(UnsupportedOperationException.class, () -> {
            unmodifiableFunction.setY(0, 100.0);
        });

        assertThrows(UnsupportedOperationException.class, () -> {
            unmodifiableFunction.setY(1, 200.0);
        });

        assertThrows(UnsupportedOperationException.class, () -> {
            unmodifiableFunction.setY(2, 300.0);
        });
    }

    // Проверка всех методов изменения
    @Test
    void testCreateUnmodifiableAllModificationMethods() {
        TabulatedFunctionFactory factory = new ArrayTabulatedFunctionFactory();

        double[] xValues = {0.0, 1.0, 2.0, 3.0};
        double[] yValues = {0.0, 1.0, 4.0, 9.0};

        TabulatedFunction unmodifiableFunction = factory.createUnmodifiable(xValues, yValues);

        // Все методы изменения должны бросать исключения

        // setY по индексу
        assertThrows(UnsupportedOperationException.class, () ->
                unmodifiableFunction.setY(0, 100.0));

        // Проверяем, что исходные значения не изменились
        assertEquals(0.0, unmodifiableFunction.getY(0));
        assertEquals(1.0, unmodifiableFunction.getY(1));
        assertEquals(4.0, unmodifiableFunction.getY(2));
        assertEquals(9.0, unmodifiableFunction.getY(3));
    }

    // Комбинированная проверка с исключительными ситуациями
    @Test
    void testCreateUnmodifiableEdgeCasesAndExceptions() {
        TabulatedFunctionFactory factory = new ArrayTabulatedFunctionFactory();

        double[] xValues = {-2.0, -1.0, 0.0, 1.0, 2.0};
        double[] yValues = {4.0, 1.0, 0.0, 1.0, 4.0};

        TabulatedFunction unmodifiableFunction = factory.createUnmodifiable(xValues, yValues);

        // Проверяем корректность данных
        assertEquals(4.0, unmodifiableFunction.apply(-2.0));
        assertEquals(1.0, unmodifiableFunction.apply(-1.0));
        assertEquals(0.0, unmodifiableFunction.apply(0.0));
        assertEquals(1.0, unmodifiableFunction.apply(1.0));
        assertEquals(4.0, unmodifiableFunction.apply(2.0));

        // Проверяем интерполяцию для отрицательных значений
        assertEquals(2.5, unmodifiableFunction.apply(-1.5), 0.0001);
        assertEquals(0.5, unmodifiableFunction.apply(-0.5), 0.0001);

        // Убеждаемся, что функция действительно немодифицируемая
        // Попытка изменить любое значение должна бросать исключение
        for (int i = 0; i < unmodifiableFunction.getCount(); i++) {
            final int index = i;
            assertThrows(UnsupportedOperationException.class, () ->
                    unmodifiableFunction.setY(index, 999.0));
        }

        // Проверяем, что после попыток изменений исходные данные остались нетронутыми
        assertEquals(4.0, unmodifiableFunction.getY(0));
        assertEquals(1.0, unmodifiableFunction.getY(1));
        assertEquals(0.0, unmodifiableFunction.getY(2));
        assertEquals(1.0, unmodifiableFunction.getY(3));
        assertEquals(4.0, unmodifiableFunction.getY(4));
    }
}