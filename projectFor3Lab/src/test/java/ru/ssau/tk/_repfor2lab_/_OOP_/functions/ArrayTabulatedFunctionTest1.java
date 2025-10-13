package ru.ssau.tk._repfor2lab_._OOP_.functions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ArrayTabulatedFunctionTest1 {

    private final double[] xValues = {1.0, 2.0, 3.0, 4.0, 5.0};
    private final double[] yValues = {2.0, 4.0, 6.0, 8.0, 10.0};
    private final ArrayTabulatedFunction function;

    public ArrayTabulatedFunctionTest1() {
        function = new ArrayTabulatedFunction(xValues, yValues);
    }

    @Test
    void testConstructorWithMathFunction() {
        // Тест с нормальными параметрами
        MathFunction source = x -> x * x;
        ArrayTabulatedFunction func = new ArrayTabulatedFunction(source, 0.0, 4.0, 5);

        assertEquals(5, func.getCount());
        assertEquals(0.0, func.leftBound(), 1e-9);
        assertEquals(4.0, func.rightBound(), 1e-9);

        // Проверка корректности вычисленных значений
        assertEquals(0.0, func.getY(0), 1e-9);  // 0² = 0
        assertEquals(4.0, func.getY(2), 1e-9);  // 2² = 4
        assertEquals(16.0, func.getY(4), 1e-9); // 4² = 16

        // Тест с xFrom > xTo (должен корректно обработать)
        ArrayTabulatedFunction funcReversed = new ArrayTabulatedFunction(source, 4.0, 0.0, 5);
        assertEquals(0.0, funcReversed.leftBound(), 1e-9);
        assertEquals(4.0, funcReversed.rightBound(), 1e-9);

        // Тест с одинаковыми xFrom и xTo
        ArrayTabulatedFunction funcSame = new ArrayTabulatedFunction(source, 2.0, 2.0, 3);
        assertEquals(3, funcSame.getCount());
        assertEquals(2.0, funcSame.getX(0), 1e-9);
        assertEquals(2.0, funcSame.getX(1), 1e-9);
        assertEquals(2.0, funcSame.getX(2), 1e-9);
        assertEquals(4.0, funcSame.getY(0), 1e-9); // 2² = 4
    }

    @Test
    void testGetX() {
        assertEquals(1.0, function.getX(0), 1e-9);
        assertEquals(3.0, function.getX(2), 1e-9);
        assertEquals(5.0, function.getX(4), 1e-9);

        // Тест граничных случаев
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            function.getX(-1);
        });
        Assertions.assertEquals("Индекс выходит за пределы", exception.getMessage());// Индекс < 0
        Exception exception2 = assertThrows(IllegalArgumentException.class, () -> {
            function.getX(10);
        });
        Assertions.assertEquals("Индекс выходит за пределы", exception2.getMessage());// Индекс >= count
    }

    @Test
    void testGetY() {
        assertEquals(2.0, function.getY(0), 1e-9);
        assertEquals(6.0, function.getY(2), 1e-9);
        assertEquals(10.0, function.getY(4), 1e-9);

        // Тест граничных случаев
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            function.getX(-1);
        });
        Assertions.assertEquals("Индекс выходит за пределы", exception.getMessage());// Индекс < 0
        Exception exception2 = assertThrows(IllegalArgumentException.class, () -> {
            function.getY(10);
        });
        Assertions.assertEquals("Индекс выходит за пределы", exception2.getMessage());// Индекс >= count
    }

    @Test
    void testSetY() {
        // Корректное изменение значения
        function.setY(2, 15.0);
        assertEquals(15.0, function.getY(2), 1e-9);

        // Проверка, что другие значения не изменились
        assertEquals(2.0, function.getY(0), 1e-9);
        assertEquals(4.0, function.getY(1), 1e-9);

        // Тест с некорректными индексами
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            function.setY(-1, 100.0);
        });
        Assertions.assertEquals("Индекс выходит за пределы", exception.getMessage());// Индекс < 0
        Exception exception2 = assertThrows(IllegalArgumentException.class, () -> {
            function.setY(10, 100.0);
        });
        Assertions.assertEquals("Индекс выходит за пределы", exception2.getMessage()); // Индекс >= count

        // Проверяем, что массив не изменился
        assertEquals(15.0, function.getY(2), 1e-9);
    }

    @Test
    void testIndexOfX() {
        assertEquals(0, function.indexOfX(1.0));
        assertEquals(2, function.indexOfX(3.0));
        assertEquals(4, function.indexOfX(5.0));
        assertEquals(-1, function.indexOfX(0.0));  // Несуществующее значение
        assertEquals(-1, function.indexOfX(10.0)); // Несуществующее значение
    }

    @Test
    void testIndexOfY() {
        assertEquals(0, function.indexOfY(2.0));
        assertEquals(2, function.indexOfY(6.0));
        assertEquals(4, function.indexOfY(10.0));
        assertEquals(-1, function.indexOfY(0.0));  // Несуществующее значение
        assertEquals(-1, function.indexOfY(15.0)); // Несуществующее значение
    }

    @Test
    void testFloorIndexOfX() {
        // Точное совпадение
        assertEquals(0, function.floorIndexOfX(1.0));
        assertEquals(2, function.floorIndexOfX(3.0));

        // Значения между узлами
        assertEquals(0, function.floorIndexOfX(1.5)); // Между 1.0 и 2.0
        assertEquals(1, function.floorIndexOfX(2.5)); // Между 2.0 и 3.0
        assertEquals(3, function.floorIndexOfX(4.5)); // Между 4.0 и 5.0

        assertEquals(4, function.floorIndexOfX(5.5));  // Больше максимального
    }

    @Test
    void testExtrapolateLeft() {
        // Экстраполяция слева
        double result = function.extrapolateLeft(0.0);
        double expected = 2.0 + (0.0 - 1.0) / (2.0 - 1.0) * (4.0 - 2.0);
        assertEquals(expected, result, 1e-9);
    }

    @Test
    void testExtrapolateLeftWithFractions() {
        // Тест 1: Простые дроби
        double result1 = function.extrapolateLeft(0.5);
        double expected1 = 2.0 + (0.5 - 1.0) / (2.0 - 1.0) * (4.0 - 2.0);
        assertEquals(expected1, result1, 1e-9);

        // Тест 2: Отрицательное значение x
        double result2 = function.extrapolateLeft(-1.5);
        double expected2 = 2.0 + (-1.5 - 1.0) / (2.0 - 1.0) * (4.0 - 2.0);
        assertEquals(expected2, result2, 1e-9);

        // Тест 3: Дробные значения между узлами
        double result3 = function.extrapolateLeft(0.75);
        double expected3 = 2.0 + (0.75 - 1.0) / (2.0 - 1.0) * (4.0 - 2.0);
        assertEquals(expected3, result3, 1e-9);
    }

    @Test
    void testExtrapolateRightWithFractions() {
        // Тест 1: Простые дроби
        double result1 = function.extrapolateRight(5.5);
        double expected1 = 8.0 + (5.5 - 4.0) / (5.0 - 4.0) * (10.0 - 8.0);
        assertEquals(expected1, result1, 1e-9);

        // Тест 2: Большое значение x
        double result2 = function.extrapolateRight(7.25);
        double expected2 = 8.0 + (7.25 - 4.0) / (5.0 - 4.0) * (10.0 - 8.0);
        assertEquals(expected2, result2, 1e-9);

        // Тест 3: Значение близко к правой границе
        double result3 = function.extrapolateRight(5.1);
        double expected3 = 8.0 + (5.1 - 4.0) / (5.0 - 4.0) * (10.0 - 8.0);
        assertEquals(expected3, result3, 1e-9);
    }

    @Test
    void testExtrapolateRight() {
        // Экстраполяция справа
        double result = function.extrapolateRight(6.0);
        double expected = 8.0 + (6.0 - 4.0) / (5.0 - 4.0) * (10.0 - 8.0);
        assertEquals(expected, result, 1e-9);
    }

    @Test
    void testInterpolateWithFractions() {
        // Тест 1: Интерполяция в середине между узлами
        double result1 = function.interpolate(1.5, 0);
        double expected1 = 2.0 + (1.5 - 1.0) / (2.0 - 1.0) * (4.0 - 2.0);
        assertEquals(expected1, result1, 1e-9);

        // Тест 2: Интерполяция ближе к левому узлу
        double result2 = function.interpolate(1.25, 0);
        double expected2 = 2.0 + (1.25 - 1.0) / (2.0 - 1.0) * (4.0 - 2.0);
        assertEquals(expected2, result2, 1e-9);

        // Тест 3: Интерполяция ближе к правому узлу
        double result3 = function.interpolate(1.75, 0);
        double expected3 = 2.0 + (1.75 - 1.0) / (2.0 - 1.0) * (4.0 - 2.0);
        assertEquals(expected3, result3, 1e-9);

        // Дополнительный тест для другого интервала
        double result4 = function.interpolate(3.5, 2);
        double expected4 = 6.0 + (3.5 - 3.0) / (4.0 - 3.0) * (8.0 - 6.0);
        assertEquals(expected4, result4, 1e-9);
    }

    @Test
    void testInterpolateWithFourParameters() {
        // Интерполяция между двумя точками
        double result = function.interpolate(2.5, 2.0, 3.0, 4.0, 6.0);
        double expected = 4.0 + (2.5 - 2.0) / (3.0 - 2.0) * (6.0 - 4.0);
        assertEquals(expected, result, 1e-9);
    }

    @Test
    void testInterpolateWithOneParameter() {
        // Интерполяция внутри диапазона
        double result = function.interpolate(2.5);
        double expected = 4.0 + (2.5 - 2.0) / (3.0 - 2.0) * (6.0 - 4.0);
        assertEquals(expected, result, 1e-9);

        // Интерполяция в существующей точке
        assertEquals(4.0, function.interpolate(2.0), 1e-9);
        assertEquals(6.0, function.interpolate(3.0), 1e-9);
    }
}
