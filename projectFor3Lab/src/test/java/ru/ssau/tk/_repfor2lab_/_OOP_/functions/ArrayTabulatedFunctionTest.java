package ru.ssau.tk._repfor2lab_._OOP_.functions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.ssau.tk._repfor2lab_._OOP_.exceptions.DifferentLengthOfArraysException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ArrayTabulatedFunctionTest {
    @Test
    void ArrayTest1(){
        double[] xValues = {0.00, 0.20, 0.40, 0.60, 0.80};
        double[] yValues = {1.00, 1.179, 1.310, 1.390, 1.414};
        var TestedVar = new ArrayTabulatedFunction(xValues, yValues);
        assertEquals(1.4088, TestedVar.apply(0.77), 0.1);
    }
    @Test
    void ArrayTest2(){
        double[] xValues = {0.00, 0.20, 0.40, 0.60, 0.80};
        double[] yValues = {0.00, 0.198, 0.388, 0.564, 0.717};
        var TestedVar = new ArrayTabulatedFunction(xValues, yValues);
        assertEquals(0.342, TestedVar.apply(0.35), 0.1);
    }
    @Test
    void ArrayTest3(){
        double[] xValues = {1.00, 1.20, 1.40, 1.60, 1.80};
        double[] yValues = {2.718, 3.320, 4.055, 4.953, 6.050};
        var TestedVar = new ArrayTabulatedFunction(xValues, yValues);
        assertEquals(4.482, TestedVar.apply(1.5), 0.1);
    }
    @Test
    void ArrayTest4(){
        double[] xValues = {2.00, 2.10, 2.20, 2.40, 2.50};
        double[] yValues = {0.135, 0.122, 0.111, 0.091, 0.082};
        var TestedVar = new ArrayTabulatedFunction(xValues, yValues);
        assertEquals(0.100, TestedVar.apply(2.3), 0.1);
    }
    @Test
    void ArrayTest5(){
        double[] xValues = {0.2, 0.4, 0.6, 0.8, 0.9};
        double[] yValues = {1.221, 1.492, 1.822, 2.226, 2.460};
        var TestedVar = new ArrayTabulatedFunction(xValues, yValues);
        assertEquals(2.718, TestedVar.apply(1.0), 0.1);
    }
    @Test
    void ArrayTest6(){
        double[] xValues = {0.0, 0.5, 1.0, 1.5, 2.0};
        double[] yValues = {1.000, 1.649, 2.718, 4.482, 7.389};
        var TestedVar = new ArrayTabulatedFunction(xValues, yValues);
        assertEquals(0.351, TestedVar.apply(-0.5), 0.1);
    }
    @Test
    void ArrayTest7(){
        double[] xValues = {1.0, 2.0, 3.0, 4.0, 5.0};
        double[] yValues = {5.0, 7.0, 9.0, 11.0, 13.0};
        var TestedVar = new ArrayTabulatedFunction(xValues, yValues);
        assertEquals(3.0, TestedVar.apply(0.0), 0.1);
    }
    @Test
    void ArrayTest8(){
        double[] xValues = {1.0};
        double[] yValues = {5.0};

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new ArrayTabulatedFunction(xValues, yValues);
        });
        Assertions.assertEquals("Длина массивов меньше минимальной возможной", exception.getMessage());

    }

    @Test
    void ArrayTest9(){
        double[] xValues = {1.0, 2.0, 3.0, 4.0, 5.0};
        double[] yValues = {5.0, 7.0, 9.0, 11.0, 13.0};
        var TestedVar = new ArrayTabulatedFunction(xValues, yValues);
        assertEquals(11.0, TestedVar.apply(4.0), 0.1);
    }
    @Test
    void ArrayTest10(){
        MathFunction f =  x -> x*x +2*x-1;
        var TestedVar = new ArrayTabulatedFunction(f, 0, 3, 100);
//      for (var i =0;i<100;++i) System.out.println(TestedVar.getX(i) + " " + TestedVar.getY(i));
        assertEquals(-0.56, TestedVar.apply(0.2), 0.1);
    }
    @Test
    void ArrayTest11(){
        MathFunction f = x -> (Math.pow(4, x)-2*x+1);
        var TestedVar = new ArrayTabulatedFunction(f, -5, 3, 100);
        assertNotEquals(548.276, TestedVar.apply(4.56), 0.1);
    }
    @Test
    void ArrayTest12(){
        MathFunction f = x -> -2*x+1;
        var TestedVar = new ArrayTabulatedFunction(f, -5, 3, 100);
        assertEquals(-152.08, TestedVar.apply(76.54), 0.1);
    }
    @Test
    void ArrayTest13() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};
        var TestedVar = new ArrayTabulatedFunction(xValues, yValues);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
           TestedVar.floorIndexOfX(0.0);
        });
        Assertions.assertEquals("Икс меньше левой границы", exception.getMessage());
    }
    @Test
    void ArrayTest14() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new ArrayTabulatedFunction(xValues, yValues).getX(-1);
        });
        Assertions.assertEquals("Индекс выходит за пределы", exception.getMessage());
    }
    @Test
    void ArrayTest15() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new ArrayTabulatedFunction(xValues, yValues).getX(3);
        });
        Assertions.assertEquals("Индекс выходит за пределы", exception.getMessage());
    }
    @Test
    void ArrayTest16() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new ArrayTabulatedFunction(xValues, yValues).getY(-1);
        });
        Assertions.assertEquals("Индекс выходит за пределы", exception.getMessage());
    }
    @Test
    void ArrayTest17() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new ArrayTabulatedFunction(xValues, yValues).getY(5);
        });
        Assertions.assertEquals("Индекс выходит за пределы", exception.getMessage());
    }
    @Test
    void ArrayTest18() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new ArrayTabulatedFunction(xValues, yValues).setY(10, 10.0);
        });
        Assertions.assertEquals("Индекс выходит за пределы", exception.getMessage());
    }
    @Test
    void ArrayTest19() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new ArrayTabulatedFunction(xValues, yValues).setY(-1, 10.0);
        });
        Assertions.assertEquals("Индекс выходит за пределы", exception.getMessage());
    }
}

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

        function.remove(-1);

        // Массив не должен измениться
        assertEquals(initialCount, function.getCount());
    }

    // Тест 3: Удаление с некорректным индексом (больше размера)
    @Test
    public void testRemoveWithIndexOutOfBounds() {
        ArrayTabulatedFunction function = createTestFunction();
        int initialCount = function.getCount();

        function.remove(10);

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