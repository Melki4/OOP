package ru.ssau.tk._repfor2lab_._OOP_.functions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.ssau.tk._repfor2lab_._OOP_.exceptions.ArrayIsNotSortedException;
import ru.ssau.tk._repfor2lab_._OOP_.exceptions.DifferentLengthOfArraysException;
import ru.ssau.tk._repfor2lab_._OOP_.exceptions.InterpolationException;

import static org.junit.jupiter.api.Assertions.*;

class MockTabulatedFunctionTest {
    @Test
    void MockTest(){
        double[] xValues = {0.2, 0.6, 0.4, 0.8, 0.9};
        double[] yValues = {1.221, 1.492, 1.822, 2.226};

        Exception exception = assertThrows(DifferentLengthOfArraysException.class, () -> {
            AbstractTabulatedFunction.checkLengthIsTheSame(xValues, yValues);
        });
        Assertions.assertEquals("Массивы разной длины", exception.getMessage());

        Exception exceptionWow = assertThrows(ArrayIsNotSortedException.class, () -> {
            AbstractTabulatedFunction.checkSorted(xValues);
        });
        Assertions.assertEquals("Массив не отсортирован", exceptionWow.getMessage());
    }
    @Test
    void testConstructorWithoutParameters() {
        MockTabulatedFunction function = new MockTabulatedFunction();

        assertEquals(2, function.getCount());
        assertEquals(0.1, function.getX(0));
        assertEquals(0.5, function.getX(1));
        assertEquals(0.01, function.getY(0));
        assertEquals(0.25, function.getY(1));
    }

    @Test
    void testConstructorWithParameters() {
        MockTabulatedFunction function = new MockTabulatedFunction(1.0, 2.0, 10.0, 20.0);

        assertEquals(2, function.getCount());
        assertEquals(1.0, function.getX(0));
        assertEquals(2.0, function.getX(1));
        assertEquals(10.0, function.getY(0));
        assertEquals(20.0, function.getY(1));
    }

    @Test
    void testSetDigits() {
        MockTabulatedFunction function = new MockTabulatedFunction();
        function.setDigits(3.0, 4.0, 30.0, 40.0);

        assertEquals(3.0, function.getX(0));
        assertEquals(4.0, function.getX(1));
        assertEquals(30.0, function.getY(0));
        assertEquals(40.0, function.getY(1));
    }

    @Test
    void testFloorIndexOfX() {
        MockTabulatedFunction function = new MockTabulatedFunction(1.0, 3.0, 10.0, 30.0);

        assertEquals(0, function.floorIndexOfX(2.0)); // x < x1
        assertEquals(1, function.floorIndexOfX(4.0)); // x > x1
        assertEquals(0, function.floorIndexOfX(1.0)); // x == x0
    }

    @Test
    void testExtrapolateLeft() {
        MockTabulatedFunction function = new MockTabulatedFunction(1.0, 3.0, 10.0, 30.0);

        double result = function.extrapolateLeft(0.0);
        double expected = (10.0 * (3.0 - 0.0) - 30.0 * (1.0 - 0.0)) / (3.0 - 1.0);
        assertEquals(expected, result, 1e-9);
    }

    @Test
    void testExtrapolateRight() {
        MockTabulatedFunction function = new MockTabulatedFunction(1.0, 3.0, 10.0, 30.0);

        double result = function.extrapolateRight(4.0);
        double expected = 10.0 + (4.0 - 1.0) / (3.0 - 1.0) * (30.0 - 10.0);
        assertEquals(expected, result, 1e-9);
    }

    @Test
    void testInterpolate() {
        MockTabulatedFunction function = new MockTabulatedFunction(1.0, 3.0, 10.0, 30.0);

        // Нормальная интерполяция
        double result = function.interpolate(2.0, 0);
        double expected = 10.0 + (30.0 - 10.0) / (3.0 - 1.0) * (2.0 - 1.0);
        assertEquals(expected, result, 1e-9);

        // Исключение при неверном floorIndex
        assertThrows(InterpolationException.class, () -> function.interpolate(2.0, 3));
        assertThrows(InterpolationException.class, () -> function.interpolate(2.0, -1));
    }

    @Test
    void testGetX() {
        MockTabulatedFunction function = new MockTabulatedFunction(1.0, 2.0, 10.0, 20.0);

        assertEquals(1.0, function.getX(0));
        assertEquals(2.0, function.getX(1));

        // Исключение при неверном индексе
        assertThrows(RuntimeException.class, () -> function.getX(-1));
        assertThrows(RuntimeException.class, () -> function.getX(2));
    }

    @Test
    void testGetY() {
        MockTabulatedFunction function = new MockTabulatedFunction(1.0, 2.0, 10.0, 20.0);

        assertEquals(10.0, function.getY(0));
        assertEquals(20.0, function.getY(1));

        // Исключение при неверном индексе
        assertThrows(RuntimeException.class, () -> function.getY(-1));
        assertThrows(RuntimeException.class, () -> function.getY(2));
    }

    @Test
    void testSetY() {
        MockTabulatedFunction function = new MockTabulatedFunction(1.0, 2.0, 10.0, 20.0);

        // Метод не реализован, но должен бросать исключение при неверном индексе
        assertThrows(RuntimeException.class, () -> function.setY(-1, 15.0));
        assertThrows(RuntimeException.class, () -> function.setY(2, 15.0));
    }

    @Test
    void testIndexOfX() {
        MockTabulatedFunction function = new MockTabulatedFunction(1.0, 2.0, 10.0, 20.0);

        assertEquals(0, function.indexOfX(1.0));
        assertEquals(1, function.indexOfX(2.0));
        assertEquals(-1, function.indexOfX(3.0)); // не найден
    }

    @Test
    void testIndexOfY() {
        MockTabulatedFunction function = new MockTabulatedFunction(1.0, 2.0, 10.0, 20.0);

        assertEquals(0, function.indexOfY(10.0));
        assertEquals(1, function.indexOfY(20.0));
        assertEquals(-1, function.indexOfY(30.0)); // не найден
    }

    @Test
    void testLeftBound() {
        MockTabulatedFunction function = new MockTabulatedFunction(1.0, 2.0, 10.0, 20.0);
        assertEquals(1.0, function.leftBound());
    }

    @Test
    void testRightBound() {
        MockTabulatedFunction function = new MockTabulatedFunction(1.0, 2.0, 10.0, 20.0);
        assertEquals(2.0, function.rightBound());
    }

    @Test
    void testIterator() {
        MockTabulatedFunction function = new MockTabulatedFunction();

        assertThrows(UnsupportedOperationException.class, () -> function.iterator());
    }
}