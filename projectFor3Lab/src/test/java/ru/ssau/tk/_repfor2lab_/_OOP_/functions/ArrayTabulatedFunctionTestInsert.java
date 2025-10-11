package ru.ssau.tk._repfor2lab_._OOP_.functions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArrayTabulatedFunctionTestInsert {

    // Вспомогательный метод для создания тестовой функции
    private ArrayTabulatedFunction createTestFunction() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        return new ArrayTabulatedFunction(xValues, yValues);
    }

    // Тест 1: Вставка существующего X (должен обновить Y)
    @Test
    public void testInsertExistingX() {
        ArrayTabulatedFunction function = createTestFunction();

        // Проверяем начальное состояние
        assertEquals(20.0, function.getY(1)); // y при x=2.0

        // Вставляем новое значение для существующего x
        function.insert(2.0, 25.0);

        // Проверяем, что значение обновилось
        assertEquals(25.0, function.getY(1));
        // Проверяем, что размер не изменился
        assertEquals(3, function.getCount());
    }

    // Тест 2: Вставка в начало (x < leftBound())
    @Test
    public void testInsertAtBeginning() {
        ArrayTabulatedFunction function = createTestFunction();

        function.insert(0.5, 5.0);

        // Проверяем размер
        assertEquals(4, function.getCount());
        // Проверяем, что новый элемент в начале
        assertEquals(0.5, function.getX(0));
        assertEquals(5.0, function.getY(0));
        // Проверяем порядок остальных элементов
        assertEquals(1.0, function.getX(1));
        assertEquals(2.0, function.getX(2));
        assertEquals(3.0, function.getX(3));
    }

    // Тест 3: Вставка в конец (x > rightBound())
    @Test
    public void testInsertAtEnd() {
        ArrayTabulatedFunction function = createTestFunction();

        function.insert(4.0, 40.0);

        // Проверяем размер
        assertEquals(4, function.getCount());
        // Проверяем, что новый элемент в конце
        assertEquals(4.0, function.getX(3));
        assertEquals(40.0, function.getY(3));
        // Проверяем порядок остальных элементов
        assertEquals(1.0, function.getX(0));
        assertEquals(2.0, function.getX(1));
        assertEquals(3.0, function.getX(2));
    }

    // Тест 4: Вставка в середину
    @Test
    public void testInsertInMiddle() {

        ArrayTabulatedFunction function = createTestFunction();

        function.insert(1.5, 15.0);

        // Проверяем размер
        assertEquals(4, function.getCount());
        // Проверяем порядок элементов
        assertEquals(1.0, function.getX(0));
        assertEquals(1.5, function.getX(1));
        assertEquals(2.0, function.getX(2));
        assertEquals(3.0, function.getX(3));
        // Проверяем соответствующие Y значения
        assertEquals(15.0, function.getY(1));
    }

    // Тест 5: Вставка в пустую функцию
    @Test
    public void testInsertIntoEmptyFunction() {
        double[] xValues = {};
        double[] yValues = {};
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new ArrayTabulatedFunction(xValues, yValues);
        });
        Assertions.assertEquals("Длина массивов меньше минимальной возможной", exception.getMessage());
    }

    // Тест 6: Вставка нескольких элементов подряд
    @Test
    public void testInsertMultipleElements() {
        ArrayTabulatedFunction function = createTestFunction();

        function.insert(0.5, 5.0);  // в начало
        function.insert(2.5, 25.0); // в середину
        function.insert(4.0, 40.0); // в конец

        assertEquals(6, function.getCount());

        // Проверяем правильный порядок
        double[] expectedX = {0.5, 1.0, 2.0, 2.5, 3.0, 4.0};
        double[] expectedY = {5.0, 10.0, 20.0, 25.0, 30.0, 40.0};

        for (int i = 0; i < expectedX.length; i++) {
            assertEquals(expectedX[i], function.getX(i));
            assertEquals(expectedY[i], function.getY(i));
        }
    }

    // Тест 7: Вставка элемента с тем же X после вставки других элементов
    @Test
    public void testUpdateAfterInsert() {
        ArrayTabulatedFunction function = createTestFunction();

        // Вставляем новый элемент
        function.insert(2.5, 25.0);
        assertEquals(25.0, function.getY(2)); // проверяем вставку

        // Обновляем значение для того же X
        function.insert(2.5, 30.0);
        assertEquals(30.0, function.getY(2)); // проверяем обновление
        assertEquals(4, function.getCount()); // размер не должен измениться
    }

    // Тест 8: Вставка элемента с граничным значением
    @Test
    public void testInsertBoundaryValue() {
        ArrayTabulatedFunction function = createTestFunction();

        // Вставляем элемент, равный leftBound (должен обновиться)
        function.insert(1.0, 15.0);
        assertEquals(15.0, function.getY(0));
        assertEquals(3, function.getCount());

        // Вставляем элемент, равный rightBound (должен обновиться)
        function.insert(3.0, 35.0);
        assertEquals(35.0, function.getY(2));
        assertEquals(3, function.getCount());
    }

    // Тест 9: Проверка корректности работы после множественных операций
    @Test
    public void testMultipleOperationsConsistency() {
        ArrayTabulatedFunction function = createTestFunction();

        // Выполняем различные операции вставки
        function.insert(0.0, 0.0);   // начало
        function.insert(1.5, 15.0);  // середина
        function.insert(2.0, 22.0);  // существующий - обновление
        function.insert(4.0, 40.0);  // конец


        // Проверяем конечное состояние
        assertEquals(6, function.getCount());

        // Проверяем, что все X значения упорядочены
        for (int i = 0; i < function.getCount() - 1; i++) {
            assertTrue(function.getX(i) < function.getX(i + 1));
        }

        // Проверяем конкретные значения
        assertEquals(22.0, function.getY(3)); // обновленное значение для x=2.0
    }
}
