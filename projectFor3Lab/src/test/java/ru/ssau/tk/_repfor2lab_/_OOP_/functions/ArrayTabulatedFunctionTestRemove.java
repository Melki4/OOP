package ru.ssau.tk._repfor2lab_._OOP_.functions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArrayTabulatedFunctionTestRemove {

    // Вспомогательный метод для создания тестовой функции
    private ArrayTabulatedFunction createTestFunction() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0, 5.0};
        double[] yValues = {10.0, 20.0, 30.0, 40.0, 50.0};
        return new ArrayTabulatedFunction(xValues, yValues);
    }

    // Тест 2: Удаление с некорректным индексом (отрицательный)
    @Test
    public void testRemoveWithNegativeIndex() {
        ArrayTabulatedFunction function = createTestFunction();
        int initialCount = function.getCount();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            function.remove(-1);
        });
        Assertions.assertEquals("Неверный индекс для удаления", exception.getMessage());

        // Массив не должен измениться
        assertEquals(initialCount, function.getCount());
    }

    // Тест 3: Удаление с некорректным индексом (больше размера)
    @Test
    public void testRemoveWithIndexOutOfBounds() {
        ArrayTabulatedFunction function = createTestFunction();
        int initialCount = function.getCount();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            function.remove(10);
        });
        Assertions.assertEquals("Неверный индекс для удаления", exception.getMessage());

        // Массив не должен измениться
        assertEquals(initialCount, function.getCount());
    }

    // Тест 4: Удаление первого элемента (index = 0)
    @Test
    public void testRemoveFirstElement() {
        ArrayTabulatedFunction function = createTestFunction();

        function.remove(0);

        assertEquals(4, function.getCount());
        // Проверяем, что первый элемент теперь второй из исходного массива
        assertEquals(2.0, function.getX(0));
        assertEquals(20.0, function.getY(0));
        // Проверяем порядок остальных элементов
        assertEquals(3.0, function.getX(1));
        assertEquals(4.0, function.getX(2));
        assertEquals(5.0, function.getX(3));
    }

    // Тест 5: Удаление последнего элемента (index = count-1)
    @Test
    public void testRemoveLastElement() {
        ArrayTabulatedFunction function = createTestFunction();

        function.remove(4); // последний элемент

        assertEquals(4, function.getCount());
        // Проверяем, что последний элемент теперь предпоследний из исходного массива
        assertEquals(4.0, function.getX(3));
        assertEquals(40.0, function.getY(3));
        // Проверяем, что первые элементы остались без изменений
        assertEquals(1.0, function.getX(0));
        assertEquals(2.0, function.getX(1));
        assertEquals(3.0, function.getX(2));
    }

    // Тест 6: Удаление элемента из середины
    @Test
    public void testRemoveMiddleElement() {
        ArrayTabulatedFunction function = createTestFunction();

        function.remove(2); // удаляем элемент с x=3.0, y=30.0

        assertEquals(4, function.getCount());
        // Проверяем порядок элементов
        assertEquals(1.0, function.getX(0));
        assertEquals(2.0, function.getX(1));
        assertEquals(4.0, function.getX(2)); // этот элемент был на позиции 3
        assertEquals(5.0, function.getX(3)); // этот элемент был на позиции 4
        // Проверяем соответствующие Y значения
        assertEquals(40.0, function.getY(2));
        assertEquals(50.0, function.getY(3));
    }

    // Тест 8: Удаление нескольких элементов подряд
    @Test
    public void testRemoveMultipleElements() {
        ArrayTabulatedFunction function = createTestFunction();

        // Удаляем второй элемент (index = 1)
        function.remove(1);
        assertEquals(4, function.getCount());
        assertEquals(3.0, function.getX(1)); // теперь на позиции 1 элемент с x=3.0

        // Удаляем первый элемент
        function.remove(0);
        assertEquals(3, function.getCount());
        assertEquals(3.0, function.getX(0)); // теперь на позиции 0 элемент с x=3.0

        // Удаляем последний элемент
        function.remove(2);
        assertEquals(2, function.getCount());
        assertEquals(3.0, function.getX(0));
        assertEquals(4.0, function.getX(1));
    }

    // Тест 9: Удаление элемента и проверка целостности данных
    @Test
    public void testDataIntegrityAfterRemove() {
        ArrayTabulatedFunction function = createTestFunction();

        function.remove(1); // удаляем элемент с x=2.0

        assertEquals(4, function.getCount());

        // Проверяем, что все оставшиеся пары X-Y соответствуют друг другу
        for (int i = 0; i < function.getCount(); i++) {
            double x = function.getX(i);
            double expectedY = x * 10; // в нашей тестовой функции y = x * 10
            assertEquals(expectedY, function.getY(i), 0.0001);
        }
    }

    // Тест 10: Удаление всех элементов по одному
    @Test
    public void testRemoveAllElements() {
        double[] xValues = {1.0, 2.0};
        double[] yValues = {10.0, 20.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        function.remove(0);
        assertEquals(1, function.getCount());
        assertEquals(2.0, function.getX(0));

        function.remove(0);
        assertEquals(0, function.getCount());

        // Попытка получить элемент из пустого массива должна выдать ошибку
        Exception exception = assertThrows(NullPointerException.class, () -> {
            function.getX(0);
        });
        Assertions.assertEquals("Обращение к пустому массиву", exception.getMessage());
    }

    // Тест 11: Удаление элемента и проверка границ функции
    @Test
    public void testBoundsAfterRemove() {
        ArrayTabulatedFunction function = createTestFunction();

        // Проверяем начальные границы
        assertEquals(1.0, function.leftBound());
        assertEquals(5.0, function.rightBound());

        // Удаляем первый элемент
        function.remove(0);
        assertEquals(2.0, function.leftBound());
        assertEquals(5.0, function.rightBound());

        // Удаляем последний элемент
        function.remove(3); // теперь count = 4, последний элемент имеет индекс 3
        assertEquals(2.0, function.leftBound());
        assertEquals(4.0, function.rightBound());
    }

    // Тест 12: Удаление элемента и проверка метода indexOfX
    @Test
    public void testIndexOfXAfterRemove() {
        ArrayTabulatedFunction function = createTestFunction();

        // Проверяем индексы до удаления
        assertEquals(2, function.indexOfX(3.0));

        // Удаляем элемент с x=2.0 (index = 1)
        function.remove(1);

        // Проверяем индексы после удаления
        assertEquals(-1, function.indexOfX(2.0)); // удаленный элемент
        assertEquals(1, function.indexOfX(3.0)); // сместился на позицию влево
        assertEquals(2, function.indexOfX(4.0)); // сместился на позицию влево
    }

    // Тест 13: Удаление с последующими операциями
    @Test
    public void testOperationsAfterRemove() {
        ArrayTabulatedFunction function = createTestFunction();

        function.remove(1); // удаляем второй элемент

        // Проверяем, что функция все еще работает корректно
        assertEquals(4, function.getCount());

        // Проверяем получение значений по индексу
        assertEquals(1.0, function.getX(0));
        assertEquals(3.0, function.getX(1));
        assertEquals(4.0, function.getX(2));
        assertEquals(5.0, function.getX(3));

        // Проверяем получение значений по X
        assertEquals(10.0, function.getY(0));
        assertEquals(30.0, function.getY(1));
        assertEquals(40.0, function.getY(2));
        assertEquals(50.0, function.getY(3));
    }

    // Тест 14: Удаление элемента с проверкой floorIndexOfX
    @Test
    public void testFloorIndexOfXAfterRemove() {
        ArrayTabulatedFunction function = createTestFunction();

        // Удаляем элемент с x=3.0
        function.remove(2);

        // Проверяем floorIndexOfX для различных значений
        assertEquals(0, function.floorIndexOfX(1.5));
        assertEquals(1, function.floorIndexOfX(2.5)); // теперь элементов меньше
        assertEquals(2, function.floorIndexOfX(4.5));
    }

    // Тест 15: Удаление с минимальным размером массива
    @Test
    public void testRemoveWithMinimalArray() {
        double[] xValues = {1.0, 2.0};
        double[] yValues = {10.0, 20.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        function.remove(0);

        assertEquals(1, function.getCount());
        assertEquals(2.0, function.getX(0));
        assertEquals(20.0, function.getY(0));
    }


    // Тест 16: Последовательное удаление из разных позиций
    @Test
    public void testSequentialRemoveFromDifferentPositions() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {10.0, 20.0, 30.0, 40.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        // Удаляем из середины
        function.remove(1);
        assertEquals(3, function.getCount());
        assertArrayEquals(new double[]{1.0, 3.0, 4.0},
                new double[]{function.getX(0), function.getX(1), function.getX(2)}, 0.0001);

        // Удаляем последний
        function.remove(2);
        assertEquals(2, function.getCount());
        assertArrayEquals(new double[]{1.0, 3.0},
                new double[]{function.getX(0), function.getX(1)}, 0.0001);

        // Удаляем первый
        function.remove(0);
        assertEquals(1, function.getCount());
        assertEquals(3.0, function.getX(0));
    }
}
