package ru.ssau.tk._repfor2lab_._OOP_.functions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class LinkedListTabulatedFunctionTest {

    @Test
    void testConstructorWithArrays() {
        // Тест конструктора с массивами
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {1.0, 4.0, 9.0, 16.0};

        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        assertEquals(4, function.getCount());
        assertEquals(1.0, function.getX(0));
        assertEquals(16.0, function.getY(3));
        assertEquals(1.0, function.leftBound());


        assertEquals(4.0, function.rightBound());
    }

    @Test
    void testConstructorWithMathFunction() {
        // Тест конструктора с математической функцией
        MathFunction square = x -> x * x;
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(square, 0.0, 4.0, 5);

        assertEquals(5, function.getCount());
        assertEquals(0.0, function.getX(0));
        assertEquals(4.0, function.getX(4));
        assertEquals(0.0, function.getY(0));
        assertEquals(16.0, function.getY(4));
    }

    @Test
    void testConstructorWithMathFunctionReverseRange() {
        // Тест с обратным порядком границ
        MathFunction square = x -> x * x;
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(square, 4.0, 0.0, 5);

        assertEquals(5, function.getCount());
        assertEquals(0.0, function.getX(0));
        assertEquals(4.0, function.getX(4));
    }

    @Test
    void testConstructorWithMathFunctionSinglePoint() {
        // Тест с одной точкой
        MathFunction constant = x -> 5.0;
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(constant, 2.0, 2.0, 3);

        assertEquals(3, function.getCount());
        assertEquals(2.0, function.getX(0));
        assertEquals(2.0, function.getX(2));
        assertEquals(5.0, function.getY(0));
        assertEquals(5.0, function.getY(2));
    }

    @Test
    void testGetX() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 2.0, 3.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        assertEquals(1.0, function.getX(0));
        assertEquals(2.0, function.getX(1));
        assertEquals(3.0, function.getX(2));
    }

    @Test
    void testGetY() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        assertEquals(10.0, function.getY(0));
        assertEquals(20.0, function.getY(1));
        assertEquals(30.0, function.getY(2));
    }

    @Test
    void testSetY() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 2.0, 3.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        function.setY(1, 99.0);
        assertEquals(99.0, function.getY(1));

        function.setY(2, 88.0);
        assertEquals(88.0, function.getY(2));
    }

    @Test
    void testLeftBound() {
        double[] xValues = {0.5, 1.5, 2.5};
        double[] yValues = {1.0, 2.0, 3.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        assertEquals(0.5, function.leftBound());
    }

    @Test
    void testRightBound() {
        double[] xValues = {1.0, 2.0, 3.5};
        double[] yValues = {1.0, 2.0, 3.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        assertEquals(3.5, function.rightBound());
    }

    @Test
    void testIndexOfX() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {1.0, 2.0, 3.0, 4.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        assertEquals(0, function.indexOfX(1.0));
        assertEquals(2, function.indexOfX(3.0));
        assertEquals(-1, function.indexOfX(5.0)); // Несуществующее значение
    }

    @Test
    void testIndexOfY() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        assertEquals(0, function.indexOfY(10.0));
        assertEquals(1, function.indexOfY(20.0));
        assertEquals(-1, function.indexOfY(99.0)); // Несуществующее значение
    }

    @Test
    void testFloorIndexOfX() {
        double[] xValues = {1.0, 2.0, 4.0, 5.0};
        double[] yValues = {1.0, 2.0, 3.0, 4.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        // Точное совпадение
        assertEquals(1, function.floorIndexOfX(2.0));

        // Между точками
        assertEquals(1, function.floorIndexOfX(3.0));
        assertEquals(2, function.floorIndexOfX(4.5));

        // За границами
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            assertEquals(0, function.floorIndexOfX(0.5));
        });
        Assertions.assertEquals("Икс меньше левой границы", exception.getMessage());

        assertEquals(3, function.floorIndexOfX(6.0));  // Правее правой границы
    }
    @Test
    void testConstructorWithSmallArrays() {
        double[] xValues = {1.0};
        double[] yValues = {1.0};

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new LinkedListTabulatedFunction(xValues, yValues);
        });
        assertEquals("Длина массивов меньше минимальной возможной", exception.getMessage());
    }
    @Test
    void testSetYWithNegativeIndex() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new LinkedListTabulatedFunction(xValues, yValues).setY(-1, 10.0);
        });
        assertEquals("Неверный индекс", exception.getMessage());
    }

    @Test
    void testSetYWithIndexOutOfBounds() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new LinkedListTabulatedFunction(xValues, yValues).setY(5, 10.0);
        });
        assertEquals("Неверный индекс", exception.getMessage());
    }

    @Test
    void testGetYWithNegativeIndex() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new LinkedListTabulatedFunction(xValues, yValues).getY(-1);
        });
        assertEquals("Неверный индекс", exception.getMessage());
    }

    @Test
    void testGetXWithNegativeIndex() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new LinkedListTabulatedFunction(xValues, yValues).getX(-1);
        });
        assertEquals("Неверный индекс", exception.getMessage());
    }
    @Test
    public void testIteratorWithWhileLoop() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {1.0, 4.0, 9.0, 16.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        Iterator<Point> iterator = function.iterator();
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
    public void testIteratorWithForEachLoop() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {1.0, 4.0, 9.0, 16.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        int index = 0;
        for (Point point : function) {
            assertEquals(point.x, xValues[index], 0.0001);
            assertEquals(point.y, yValues[index], 0.0001);
            index++;
        }

        assertEquals(index, xValues.length, "Нужно перебрать все элементы");
    }
    @Test
    public void testIteratorThrowsExceptionOnNoMoreElements() {
        double[] xValues = {1.0, 2.0};
        double[] yValues = {1.0, 4.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        Iterator<Point> iterator = function.iterator();

        iterator.next();
        iterator.next();

        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    public void testIteratorWithSingleElement() {
        double[] xValuesTemp = {1.0, 2.0};
        double[] yValuesTemp = {1.0, 4.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValuesTemp, yValuesTemp);
        function.remove(1);

        Iterator<Point> iterator = function.iterator();

        assertTrue(iterator.hasNext());
        Point point = iterator.next();
        assertEquals(1.0, point.x, 0.0001);
        assertEquals(1.0, point.y, 0.0001);

        assertFalse(iterator.hasNext());
    }


    @Test
    void testExtrapolateLeft() {
        double[] xValues = {2.0, 3.0, 4.0};
        double[] yValues = {4.0, 9.0, 16.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        // Экстраполяция слева
        double result = function.extrapolateLeft(1.0);
        double expected = 4.0 + (1.0 - 2.0) / (3.0 - 2.0) * (9.0 - 4.0);
        assertEquals(expected, result, 1e-10);
    }

    @Test
    void testExtrapolateRight() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        // Экстраполяция справа
        double result = function.extrapolateRight(4.0);
        double expected = 4.0 + (4.0 - 2.0) / (3.0 - 2.0) * (9.0 - 4.0);
        assertEquals(expected, result, 1e-10);
    }

    @Test
    void testInterpolateWithIndex() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        // Интерполяция между точками
        double result = function.interpolate(1.5, 0);
        double expected = 1.0 + (1.5 - 1.0) / (2.0 - 1.0) * (4.0 - 1.0);
        assertEquals(expected, result, 1e-10);
    }

    @Test
    void testInterpolateWithoutIndex() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        // Автоматическое определение индекса для интерполяции
        double result = function.apply(2.5);
        double expected = 4.0 + (2.5 - 2.0) / (3.0 - 2.0) * (9.0 - 4.0);
        assertEquals(expected, result, 1e-10);
    }

    @Test
    void testTwoNodeFunction() {
        // Тест функции с двумя точками
        double[] xValues = {1.0, 2.0};
        double[] yValues = {1.0, 4.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        assertEquals(2, function.getCount());
        assertEquals(1.0, function.leftBound());
        assertEquals(2.0, function.rightBound());

        // Проверка циклической структуры
        assertEquals(1.0, function.getX(0));
        assertEquals(2.0, function.getX(1));
        assertEquals(1.0, function.getY(0));
        assertEquals(4.0, function.getY(1));
    }

    @Test
    void testCircularStructure() {
        // Проверка циклической структуры linked list
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 2.0, 3.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        // Должны корректно работать переходы через границы
        assertEquals(1.0, function.getX(0));
        assertEquals(2.0, function.getX(1));
        assertEquals(3.0, function.getX(2));

        // И обратно к началу
        assertEquals(1.0, function.getX(3 % function.getCount()));
    }
}

