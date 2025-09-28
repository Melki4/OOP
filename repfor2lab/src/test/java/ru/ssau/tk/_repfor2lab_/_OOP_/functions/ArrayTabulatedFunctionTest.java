package ru.ssau.tk._repfor2lab_._OOP_.functions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArrayTabulatedFunctionTest {
    @Test
    void ArrayTest1(){
        double[] xValues = {0.00, 0.20, 0.40, 0.60, 0.80};
        double[] yValues = {1.00, 1.179, 1.310, 1.390, 1.414};
        var TestedVar = new ArrayTabulatedFunction(xValues, yValues);
        assertEquals(1.4088, TestedVar.clever_interpolate(0.77), 0.1);
    }
    @Test
    void ArrayTest2(){
        double[] xValues = {0.00, 0.20, 0.40, 0.60, 0.80};
        double[] yValues = {0.00, 0.198, 0.388, 0.564, 0.717};
        var TestedVar = new ArrayTabulatedFunction(xValues, yValues);
        assertEquals(0.342, TestedVar.clever_interpolate(0.35), 0.1);
    }
    @Test
    void ArrayTest3(){
        double[] xValues = {1.00, 1.20, 1.40, 1.60, 1.80};
        double[] yValues = {2.718, 3.320, 4.055, 4.953, 6.050};
        var TestedVar = new ArrayTabulatedFunction(xValues, yValues);
        assertEquals(4.482, TestedVar.clever_interpolate(1.5), 0.1);
    }
    @Test
    void ArrayTest4(){
        double[] xValues = {2.00, 2.10, 2.20, 2.40, 2.50};
        double[] yValues = {0.135, 0.122, 0.111, 0.091, 0.082};
        var TestedVar = new ArrayTabulatedFunction(xValues, yValues);
        assertEquals(0.100, TestedVar.clever_interpolate(2.3), 0.1);
    }
    @Test
    void ArrayTest5(){
        double[] xValues = {0.2, 0.4, 0.6, 0.8, 0.9};
        double[] yValues = {1.221, 1.492, 1.822, 2.226, 2.460};
        var TestedVar = new ArrayTabulatedFunction(xValues, yValues);
        assertEquals(2.718, TestedVar.clever_interpolate(1.0), 0.1);
    }
    @Test
    void ArrayTest6(){
        double[] xValues = {0.0, 0.5, 1.0, 1.5, 2.0};
        double[] yValues = {1.000, 1.649, 2.718, 4.482, 7.389};
        var TestedVar = new ArrayTabulatedFunction(xValues, yValues);
        assertEquals(0.351, TestedVar.clever_interpolate(-0.5), 0.1);
    }
    @Test
    void ArrayTest7(){
        double[] xValues = {1.0, 2.0, 3.0, 4.0, 5.0};
        double[] yValues = {5.0, 7.0, 9.0, 11.0, 13.0};
        var TestedVar = new ArrayTabulatedFunction(xValues, yValues);
        assertEquals(3.0, TestedVar.clever_interpolate(0.0), 0.1);
    }
    @Test
    void ArrayTest8(){
        double[] xValues = {1.0};
        double[] yValues = {5.0};
        var TestedVar = new ArrayTabulatedFunction(xValues, yValues);
        assertEquals(5.0, TestedVar.clever_interpolate(2.0), 0.1);
    }
    @Test
    void ArrayTest9(){
        double[] xValues = {1.0, 2.0, 3.0, 4.0, 5.0};
        double[] yValues = {5.0, 7.0, 9.0, 11.0, 13.0};
        var TestedVar = new ArrayTabulatedFunction(xValues, yValues);
        assertEquals(11.0, TestedVar.clever_interpolate(4.0), 0.1);
    }
    @Test
    void ArrayTest10(){
        MathFunction f =  x -> x*x +2*x-1;
        var TestedVar = new ArrayTabulatedFunction(f, 0, 3, 100);
        assertEquals(-0.56, TestedVar.clever_interpolate(0.2), 0.1);
    }
    @Test
    void ArrayTest11(){
        MathFunction f = x -> (Math.pow(4, x)-2*x+1);
        var TestedVar = new ArrayTabulatedFunction(f, -5, 3, 100);
        assertNotEquals(548.276, TestedVar.clever_interpolate(4.56), 0.1);
    }
    @Test
    void ArrayTest12(){
        MathFunction f = x -> -2*x+1;
        var TestedVar = new ArrayTabulatedFunction(f, -5, 3, 100);
        assertEquals(-152.08, TestedVar.clever_interpolate(76.54), 0.1);
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
    void testGetCount() {
        assertEquals(5, function.getCount());

        // Тест с функцией из одного узла
        double[] singleX = {1.0};
        double[] singleY = {2.0};
        ArrayTabulatedFunction singleFunc = new ArrayTabulatedFunction(singleX, singleY);
        assertEquals(1, singleFunc.getCount());
    }

    @Test
    void testGetX() {
        assertEquals(1.0, function.getX(0), 1e-9);
        assertEquals(3.0, function.getX(2), 1e-9);
        assertEquals(5.0, function.getX(4), 1e-9);

        // Тест граничных случаев
        assertEquals(-1.0, function.getX(-1), 1e-9); // Индекс < 0
        assertEquals(-1.0, function.getX(10), 1e-9); // Индекс >= count
    }

    @Test
    void testGetY() {
        assertEquals(2.0, function.getY(0), 1e-9);
        assertEquals(6.0, function.getY(2), 1e-9);
        assertEquals(10.0, function.getY(4), 1e-9);

        // Тест граничных случаев
        assertEquals(-1.0, function.getY(-1), 1e-9); // Индекс < 0
        assertEquals(-1.0, function.getY(10), 1e-9); // Индекс >= count
    }

    @Test
    void testSetY() {
        // Корректное изменение значения
        function.setY(2, 15.0);
        assertEquals(15.0, function.getY(2), 1e-9);

        // Проверка, что другие значения не изменились
        assertEquals(2.0, function.getY(0), 1e-9);
        assertEquals(4.0, function.getY(1), 1e-9);

        // Тест с некорректными индексами (не должно быть исключений)
        function.setY(-1, 100.0); // Индекс < 0
        function.setY(10, 100.0); // Индекс >= count

        // Проверяем, что массив не изменился
        assertEquals(15.0, function.getY(2), 1e-9);
    }

    @Test
    void testLeftBound() {
        assertEquals(1.0, function.leftBound(), 1e-9);

        // Тест с одним узлом
        double[] singleX = {5.0};
        double[] singleY = {10.0};
        ArrayTabulatedFunction singleFunc = new ArrayTabulatedFunction(singleX, singleY);
        assertEquals(5.0, singleFunc.leftBound(), 1e-9);
    }

    @Test
    void testRightBound() {
        assertEquals(5.0, function.rightBound(), 1e-9);

        // Тест с одним узлом
        double[] singleX = {5.0};
        double[] singleY = {10.0};
        ArrayTabulatedFunction singleFunc = new ArrayTabulatedFunction(singleX, singleY);
        assertEquals(5.0, singleFunc.rightBound(), 1e-9);
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

        // Граничные случаи
        assertEquals(0, function.floorIndexOfX(0.5));  // Меньше минимального
        assertEquals(4, function.floorIndexOfX(5.5));  // Больше максимального

        // Тест с одним узлом
        double[] singleX = {2.0};
        double[] singleY = {4.0};
        ArrayTabulatedFunction singleFunc = new ArrayTabulatedFunction(singleX, singleY);
        assertEquals(0, singleFunc.floorIndexOfX(1.0));
        assertEquals(0, singleFunc.floorIndexOfX(2.0));
        assertEquals(0, singleFunc.floorIndexOfX(3.0));
    }

    @Test
    void testExtrapolateLeft() {
        // Экстраполяция слева
        double result = function.extrapolateLeft(0.0);
        double expected = 2.0 + (0.0 - 1.0) / (2.0 - 1.0) * (4.0 - 2.0);
        assertEquals(expected, result, 1e-9);

        // Тест с одним узлом
        double[] singleX = {2.0};
        double[] singleY = {4.0};
        ArrayTabulatedFunction singleFunc = new ArrayTabulatedFunction(singleX, singleY);
        assertEquals(4.0, singleFunc.extrapolateLeft(1.0), 1e-9);
    }

    @Test
    void testExtrapolateRight() {
        // Экстраполяция справа
        double result = function.extrapolateRight(6.0);
        double expected = 8.0 + (6.0 - 4.0) / (5.0 - 4.0) * (10.0 - 8.0);
        assertEquals(expected, result, 1e-9);

        // Тест с одним узлом
        double[] singleX = {2.0};
        double[] singleY = {4.0};
        ArrayTabulatedFunction singleFunc = new ArrayTabulatedFunction(singleX, singleY);
        assertEquals(4.0, singleFunc.extrapolateRight(3.0), 1e-9);
    }

    @Test
    void testInterpolateWithFourParameters() {
        // Интерполяция между двумя точками
        double result = function.interpolate(2.5, 2.0, 3.0, 4.0, 6.0);
        double expected = 4.0 + (2.5 - 2.0) / (3.0 - 2.0) * (6.0 - 4.0);
        assertEquals(expected, result, 1e-9);

        // Тест с одним узлом в функции (но 4 параметра переданы)
        double[] singleX = {2.0};
        double[] singleY = {4.0};
        ArrayTabulatedFunction singleFunc = new ArrayTabulatedFunction(singleX, singleY);
        assertEquals(4.0, singleFunc.interpolate(2.5, 2.0, 3.0, 4.0, 6.0), 1e-9);
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

        // Тест с одним узлом
        double[] singleX = {2.0};
        double[] singleY = {4.0};
        ArrayTabulatedFunction singleFunc = new ArrayTabulatedFunction(singleX, singleY);
        assertEquals(4.0, singleFunc.interpolate(2.0), 1e-9);
        assertEquals(4.0, singleFunc.interpolate(2.5), 1e-9);
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
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        function.insert(1.0, 10.0);

        assertEquals(1, function.getCount());
        assertEquals(1.0, function.getX(0));
        assertEquals(10.0, function.getY(0));
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
        //for(int i =0; i< 4;++i) System.out.println(function.getX(i));
        // Выполняем различные операции вставки
        function.insert(0.0, 0.0);   // начало
        function.insert(1.5, 15.0);  // середина
        function.insert(2.0, 22.0);  // существующий - обновление
        function.insert(4.0, 40.0);  // конец
        for(int i =0; i< 6;++i) System.out.println(function.getX(i) + " " + function.getY(i));
        /*
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        */

        // Проверяем конечное состояние
        assertEquals(6, function.getCount());

        // Проверяем, что все X значения упорядочены
        for (int i = 0; i < function.getCount() - 1; i++) {
            assertTrue(function.getX(i) < function.getX(i + 1),
                    "X values should be in ascending order");
        }

        // Проверяем конкретные значения
        assertEquals(22.0, function.getY(3)); // обновленное значение для x=2.0
    }
}