package ru.ssau.tk._repfor2lab_._OOP_.functions.factory;

import org.junit.jupiter.api.Test;
import ru.ssau.tk._repfor2lab_._OOP_.functions.TabulatedFunction;

import static org.junit.jupiter.api.Assertions.*;

class TabulatedFunctionFactoryTestStrictUnmodifiable {
    // Комплексная проверка строгости и немодифицируемости
    @Test
    void testCreateStrictUnmodifiableCombinedProperties() {
        TabulatedFunctionFactory factory = new ArrayTabulatedFunctionFactory();

        double[] xValues = {1.0, 2.0, 3.0, 4.0, 5.0};
        double[] yValues = {10.0, 20.0, 30.0, 40.0, 50.0};

        TabulatedFunction strictUnmodifiable = factory.createStrictUnmodifiable(xValues, yValues);

        // Проверяем строгость - интерполяция должна бросать исключение
        assertThrows(UnsupportedOperationException.class, () -> strictUnmodifiable.apply(1.5));
        assertThrows(UnsupportedOperationException.class, () -> strictUnmodifiable.apply(2.7));
        assertThrows(UnsupportedOperationException.class, () -> strictUnmodifiable.apply(3.3));
        assertThrows(UnsupportedOperationException.class, () -> strictUnmodifiable.apply(4.8));

        // Проверяем немодифицируемость - setY должен бросать исключение
        assertThrows(UnsupportedOperationException.class, () -> strictUnmodifiable.setY(0, 100.0));
        assertThrows(UnsupportedOperationException.class, () -> strictUnmodifiable.setY(2, 300.0));
        assertThrows(UnsupportedOperationException.class, () -> strictUnmodifiable.setY(4, 500.0));

        // Проверяем, что чтение в узлах работает корректно
        assertEquals(10.0, strictUnmodifiable.apply(1.0));
        assertEquals(20.0, strictUnmodifiable.apply(2.0));
        assertEquals(30.0, strictUnmodifiable.apply(3.0));
        assertEquals(40.0, strictUnmodifiable.apply(4.0));
        assertEquals(50.0, strictUnmodifiable.apply(5.0));
    }

    // Проверка сохранения данных после множественных попыток изменений
    @Test
    void testCreateStrictUnmodifiableDataIntegrityUnderStress() {
        TabulatedFunctionFactory factory = new LinkedListTabulatedFunctionFactory();

        double[] xValues = {-2.0, -1.0, 0.0, 1.0, 2.0};
        double[] yValues = {4.0, 1.0, 0.0, 1.0, 4.0}; // функция x^2

        TabulatedFunction strictUnmodifiable = factory.createStrictUnmodifiable(xValues, yValues);

        // Сохраняем исходные значения для последующей проверки
        double[] originalValues = new double[yValues.length];
        for (int i = 0; i < yValues.length; i++) {
            originalValues[i] = strictUnmodifiable.getY(i);
        }

        // Многократные попытки нарушения строгости
        for (double x = -1.9; x < 1.9; x += 0.2) {
            if (x != -2.0 && x != -1.0 && x != 0.0 && x != 1.0 && x != 2.0) {
                final double testX = x;
                assertThrows(UnsupportedOperationException.class, () ->
                                strictUnmodifiable.apply(testX),
                        "Should throw for x = " + testX);
            }
        }

        // Проверяем, что все данные остались неизменными
        for (int i = 0; i < originalValues.length; i++) {
            assertEquals(originalValues[i], strictUnmodifiable.getY(i),
                    "Value at index " + i + " was modified");
            assertEquals(originalValues[i], strictUnmodifiable.apply(xValues[i]),
                    "Applied value at x=" + xValues[i] + " was modified");
        }
    }

    // Проверка граничных случаев и особых точек
    @Test
    void testCreateStrictUnmodifiableEdgeCases() {
        TabulatedFunctionFactory factory = new ArrayTabulatedFunctionFactory();

        // Тест с двумя точками
        double[] twoX = {0.0, 10.0};
        double[] twoY = {0.0, 100.0};
        TabulatedFunction twoPoints = factory.createStrictUnmodifiable(twoX, twoY);

        assertEquals(0.0, twoPoints.apply(0.0));
        assertEquals(100.0, twoPoints.apply(10.0));

        // Все промежуточные точки должны бросать исключение
        for (double x = 0.1; x < 10.0; x += 1.0) {
            final double testX = x;
            assertThrows(UnsupportedOperationException.class, () -> twoPoints.apply(testX));
        }

        // Модификация также должна быть запрещена
        assertThrows(UnsupportedOperationException.class, () -> twoPoints.setY(0, 50.0));
        assertThrows(UnsupportedOperationException.class, () -> twoPoints.setY(1, 150.0));
    }

    // Сравнительный тест для обеих реализаций фабрик
    @Test
    void testCreateStrictUnmodifiableBothFactoryImplementations() {
        // Тестируем ArrayTabulatedFunctionFactory
        TabulatedFunctionFactory arrayFactory = new ArrayTabulatedFunctionFactory();
        testStrictUnmodifiableBehavior(arrayFactory, "ArrayTabulatedFunctionFactory");

        // Тестируем LinkedListTabulatedFunctionFactory
        TabulatedFunctionFactory linkedListFactory = new LinkedListTabulatedFunctionFactory();
        testStrictUnmodifiableBehavior(linkedListFactory, "LinkedListTabulatedFunctionFactory");
    }

    private void testStrictUnmodifiableBehavior(TabulatedFunctionFactory factory, String factoryName) {
        double[] xValues = {1.0, 3.0, 5.0, 7.0, 9.0};
        double[] yValues = {2.0, 6.0, 10.0, 14.0, 18.0}; // линейная функция 2x

        TabulatedFunction strictUnmodifiable = factory.createStrictUnmodifiable(xValues, yValues);

        // Проверка строгости
        assertThrows(UnsupportedOperationException.class, () -> strictUnmodifiable.apply(2.0));
        assertThrows(UnsupportedOperationException.class, () -> strictUnmodifiable.apply(4.0));
        assertThrows(UnsupportedOperationException.class, () -> strictUnmodifiable.apply(6.0));
        assertThrows(UnsupportedOperationException.class, () -> strictUnmodifiable.apply(8.0));

        // Проверка немодифицируемости
        for (int i = 0; i < strictUnmodifiable.getCount(); i++) {
            final int index = i;
            assertThrows(UnsupportedOperationException.class, () -> strictUnmodifiable.setY(index, 999.0));
        }

        // Проверка корректности работы в узлах
        assertEquals(2.0, strictUnmodifiable.apply(1.0));
        assertEquals(6.0, strictUnmodifiable.apply(3.0));
        assertEquals(10.0, strictUnmodifiable.apply(5.0));
        assertEquals(14.0, strictUnmodifiable.apply(7.0));
        assertEquals(18.0, strictUnmodifiable.apply(9.0));

        // Дополнительная проверка - после всех тестов данные должны остаться неизменными
        assertEquals(2.0, strictUnmodifiable.getY(0));
        assertEquals(6.0, strictUnmodifiable.getY(1));
        assertEquals(10.0, strictUnmodifiable.getY(2));
        assertEquals(14.0, strictUnmodifiable.getY(3));
        assertEquals(18.0, strictUnmodifiable.getY(4));
    }

    @Test
    void testCreateStrictUnmodifiableWithNegativeAndFractionalValues() {
        TabulatedFunctionFactory factory = new ArrayTabulatedFunctionFactory();

        double[] xValues = {-3.5, -2.0, 0.5, 2.0, 3.5};
        double[] yValues = {-10.5, -4.0, 1.0, 4.0, 10.5};

        TabulatedFunction strictUnmodifiable = factory.createStrictUnmodifiable(xValues, yValues);

        // Проверка строгости для дробных значений
        assertThrows(UnsupportedOperationException.class, () -> strictUnmodifiable.apply(-3.0));
        assertThrows(UnsupportedOperationException.class, () -> strictUnmodifiable.apply(-1.0));
        assertThrows(UnsupportedOperationException.class, () -> strictUnmodifiable.apply(1.0));
        assertThrows(UnsupportedOperationException.class, () -> strictUnmodifiable.apply(3.0));

        // Проверка работы в узлах с отрицательными и дробными значениями
        assertEquals(-10.5, strictUnmodifiable.apply(-3.5));
        assertEquals(-4.0, strictUnmodifiable.apply(-2.0));
        assertEquals(1.0, strictUnmodifiable.apply(0.5));
        assertEquals(4.0, strictUnmodifiable.apply(2.0));
        assertEquals(10.5, strictUnmodifiable.apply(3.5));

        // Проверка немодифицируемости
        assertThrows(UnsupportedOperationException.class, () -> strictUnmodifiable.setY(0, 0.0));
        assertThrows(UnsupportedOperationException.class, () -> strictUnmodifiable.setY(2, 0.0));
        assertThrows(UnsupportedOperationException.class, () -> strictUnmodifiable.setY(4, 0.0));
    }
}