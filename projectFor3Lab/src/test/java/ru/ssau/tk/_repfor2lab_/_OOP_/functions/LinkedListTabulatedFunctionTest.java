package ru.ssau.tk._repfor2lab_._OOP_.functions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.ssau.tk._repfor2lab_._OOP_.exceptions.DifferentLengthOfArraysException;

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

class ComplexFunctionCombinationsTest {

    // Базовые математические функции для тестирования
    private final MathFunction square = x -> x * x;
    private final MathFunction linear = x -> 2 * x + 1;
    private final MathFunction sinFunction = Math::sin;
    private final MathFunction expFunction = Math::exp;
    private final MathFunction constant = x -> 5.0;

    @Test
    void testArrayAndLinkedListCombination() {
        // Создаем две табулированные функции разными способами
        double[] xValues1 = {0.0, 1.0, 2.0, 3.0, 4.0};
        double[] yValues1 = {0.0, 1.0, 4.0, 9.0, 16.0}; // x²

        ArrayTabulatedFunction arrayFunc = new ArrayTabulatedFunction(xValues1, yValues1);
        LinkedListTabulatedFunction linkedListFunc = new LinkedListTabulatedFunction(xValues1, yValues1);

        // Проверяем, что они ведут себя одинаково
        assertEquals(arrayFunc.getCount(), linkedListFunc.getCount());
        assertEquals(arrayFunc.leftBound(), linkedListFunc.leftBound(), 1e-9);
        assertEquals(arrayFunc.rightBound(), linkedListFunc.rightBound(), 1e-9);

        for (int i = 0; i < arrayFunc.getCount(); i++) {
            assertEquals(arrayFunc.getX(i), linkedListFunc.getX(i), 1e-9);
            assertEquals(arrayFunc.getY(i), linkedListFunc.getY(i), 1e-9);
        }

        // Проверяем интерполяцию
        assertEquals(arrayFunc.interpolate(1.5), linkedListFunc.interpolate(1.5), 1e-9);
        assertEquals(arrayFunc.interpolate(0.5), linkedListFunc.interpolate(0.5), 1e-9);
    }

    @Test
    void testTabulatedFunctionAsMathFunction() {
        // Используем табулированную функцию как MathFunction
        double[] xValues = {0.0, 1.0, 2.0, 3.0};
        double[] yValues = {1.0, 2.0, 4.0, 8.0}; // 2^x

        ArrayTabulatedFunction tabulatedFunc = new ArrayTabulatedFunction(xValues, yValues);

        // Создаем новую табулированную функцию на основе существующей
        ArrayTabulatedFunction composedFunc = new ArrayTabulatedFunction(
                tabulatedFunc::interpolate, 0.5, 2.5, 10
        );

        assertEquals(10, composedFunc.getCount());
        // Проверяем, что значения соответствуют ожидаемым
        assertEquals(2.0, composedFunc.interpolate(1.0), 0.1);
        assertEquals(4.0, composedFunc.interpolate(2.0), 0.1);
    }

    @Test
    void testCompositeFunctions() {
        // f(x) = x², g(x) = 2x + 1, тогда (f∘g)(x) = (2x+1)²
        double[] xValues = {-2.0, -1.0, 0.0, 1.0, 2.0};
        double[] yValues = new double[xValues.length];

        // Заполняем значениями композитной функции
        for (int i = 0; i < xValues.length; i++) {
            yValues[i] = square.apply(linear.apply(xValues[i])); // (2x+1)²
        }

        ArrayTabulatedFunction compositeArray = new ArrayTabulatedFunction(xValues, yValues);
        LinkedListTabulatedFunction compositeLinkedList = new LinkedListTabulatedFunction(xValues, yValues);

        // Проверяем значения композитной функции
        assertEquals(1.0, compositeArray.interpolate(0.0), 1e-9);   // (2*0+1)² = 1
        assertEquals(9.0, compositeArray.interpolate(1.0), 1e-9);   // (2*1+1)² = 9
        assertEquals(25.0, compositeArray.interpolate(2.0), 1e-9);  // (2*2+1)² = 25

        // Проверяем согласованность между реализациями
        assertEquals(compositeArray.interpolate(0.5), compositeLinkedList.interpolate(0.5), 1e-9);
    }

    @Test
    void testSumOfTabulatedFunctions() {
        // f(x) = x², g(x) = 2x + 1, f(x) + g(x) = x² + 2x + 1
        double[] xValues = {0.0, 1.0, 2.0, 3.0};
        double[] yValues1 = {0.0, 1.0, 4.0, 9.0};  // x²
        double[] yValues2 = {1.0, 3.0, 5.0, 7.0};  // 2x + 1

        ArrayTabulatedFunction func1 = new ArrayTabulatedFunction(xValues, yValues1);
        LinkedListTabulatedFunction func2 = new LinkedListTabulatedFunction(xValues, yValues2);

        // Создаем функцию суммы через MathFunction
        MathFunction sumFunction = x -> func1.apply(x) + func2.apply(x);

        ArrayTabulatedFunction sumTabulated = new ArrayTabulatedFunction(sumFunction, 0.0, 3.0, 10);

        // Проверяем значения суммы
        assertEquals(1.0, sumTabulated.apply(0.0), 0.1);  // 0 + 1 = 1
        assertEquals(4.0, sumTabulated.apply(1.0), 0.1);  // 1 + 3 = 4
        assertEquals(9.0, sumTabulated.apply(2.0), 0.1);  // 4 + 5 = 9
        assertEquals(16.0, sumTabulated.apply(3.0), 0.1); // 9 + 7 = 16
    }

    @Test
    void testProductOfFunctions() {
        // f(x) = sin(x), g(x) = exp(x), h(x) = sin(x) * exp(x)
        ArrayTabulatedFunction sinTabulated = new ArrayTabulatedFunction(sinFunction, 0.0, Math.PI, 20);
        ArrayTabulatedFunction expTabulated = new ArrayTabulatedFunction(expFunction, 0.0, Math.PI, 20);

        MathFunction productFunction = x -> sinTabulated.interpolate(x) * expTabulated.interpolate(x);
        ArrayTabulatedFunction productTabulated = new ArrayTabulatedFunction(productFunction, 0.0, Math.PI, 30);

        // Проверяем известные значения
        assertEquals(0.0, productTabulated.interpolate(0.0), 0.01);      // sin(0)*exp(0) = 0
        assertEquals(Math.sin(1.0) * Math.exp(1.0), productTabulated.interpolate(1.0), 0.1);
        assertEquals(0.0, productTabulated.interpolate(Math.PI), 0.01);  // sin(π)*exp(π) = 0
    }

    @Test
    void testFunctionCompositionWithDifferentImplementations() {
        // f(x) = x² (массив), g(x) = 2x+1 (список), h(x) = f(g(x))
        double[] xValuesF = {0.0, 1.0, 2.0, 3.0, 4.0};
        double[] yValuesF = {0.0, 1.0, 4.0, 9.0, 16.0};

        double[] xValuesG = {-1.0, 0.0, 1.0, 2.0, 3.0};
        double[] yValuesG = {-1.0, 1.0, 3.0, 5.0, 7.0}; // 2x+1

        ArrayTabulatedFunction f_array = new ArrayTabulatedFunction(xValuesF, yValuesF);
        LinkedListTabulatedFunction g_linkedList = new LinkedListTabulatedFunction(xValuesG, yValuesG);

        // Композиция: h(x) = f(g(x))
        MathFunction composition = x -> {
            double gx = g_linkedList.apply(x);
            return f_array.apply(gx);
        };

        ArrayTabulatedFunction composed = new ArrayTabulatedFunction(composition, -1.0, 3.0, 20);

        // Проверяем значения композиции
        assertEquals(-1.0, composed.apply(-1.0), 0.1);  // f(g(-1)) = f(-1) = -1.0 (экстраполяция)
        assertEquals(1.6, composed.apply(0.1), 0.1);    // f(g(0)) = f(1) = 1
        assertEquals(10.4, composed.apply(1.1), 0.1);    // f(g(1)) = f(3) = 9
    }

    @Test
    void testConstantFunctionCombinations() {
        // Постоянная функция в комбинациях
        ArrayTabulatedFunction constantArray = new ArrayTabulatedFunction(constant, 0.0, 5.0, 10);
        LinkedListTabulatedFunction linearLinkedList = new LinkedListTabulatedFunction(linear, 0.0, 5.0, 10);

        // Сумма постоянной и линейной функции
        MathFunction sumWithConstant = x -> constantArray.apply(x) + linearLinkedList.apply(x);
        ArrayTabulatedFunction sumTabulated = new ArrayTabulatedFunction(sumWithConstant, 0.0, 5.0, 15);

        // Проверяем: 5 + (2x + 1) = 2x + 6
        assertEquals(6.0, sumTabulated.apply(0.0), 0.1);  // 2*0 + 6 = 6
        assertEquals(8.0, sumTabulated.apply(1.0), 0.1);  // 2*1 + 6 = 8
        assertEquals(16.0, sumTabulated.apply(5.0), 0.1); // 2*5 + 6 = 16
    }

    @Test
    void testExtrapolationCombinations() {
        // Тестируем экстраполяцию в комбинированных функциях
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {2.0, 4.0, 6.0}; // 2x

        ArrayTabulatedFunction arrayFunc = new ArrayTabulatedFunction(xValues, yValues);
        LinkedListTabulatedFunction linkedFunc = new LinkedListTabulatedFunction(xValues, yValues);

        // Функция, использующая экстраполяцию обеих реализаций
        MathFunction extrapolationTest = x ->
                arrayFunc.apply(x) * linkedFunc.apply(x);

        // Проверяем экстраполяцию слева
        double leftExtrapolationArray = arrayFunc.extrapolateLeft(0.0);
        double leftExtrapolationLinked = linkedFunc.extrapolateLeft(0.0);
        assertEquals(leftExtrapolationArray * leftExtrapolationLinked,
                extrapolationTest.apply(0.0), 0.1);

        // Проверяем экстраполяцию справа
        double rightExtrapolationArray = arrayFunc.apply(4.0);
        double rightExtrapolationLinked = linkedFunc.apply(4.0);
        assertEquals(rightExtrapolationArray * rightExtrapolationLinked,
                extrapolationTest.apply(4.0), 0.1);
    }

    @Test
    void testComplexMathematicalExpression() {
        // Сложное математическое выражение с использованием табулированных функций
        ArrayTabulatedFunction quadratic = new ArrayTabulatedFunction(square, -2.0, 2.0, 20);
        ArrayTabulatedFunction trigonometric = new ArrayTabulatedFunction(sinFunction, -2.0, 2.0, 20);

        // h(x) = x² * sin(x) + exp(x)
        MathFunction complexExpression = x ->
                quadratic.interpolate(x) * trigonometric.interpolate(x) + Math.exp(x);

        ArrayTabulatedFunction complexTabulated = new ArrayTabulatedFunction(
                complexExpression, -2.0, 2.0, 50
        );

        // Проверяем в нескольких точках
        double x1 = 0.0;
        double expected1 = 0 * 0 + Math.exp(0); // 0 * 0 + 1 = 1
        assertEquals(expected1, complexTabulated.interpolate(x1), 0.1);

        double x2 = 1.0;
        double expected2 = 1 * Math.sin(1) + Math.exp(1);
        assertEquals(expected2, complexTabulated.interpolate(x2), 0.1);
    }
}

class LinkedListTabulatedFunctionTest2 {

    // Вспомогательный метод для создания тестовой функции
    private LinkedListTabulatedFunction createTestFunction() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        return new LinkedListTabulatedFunction(xValues, yValues);
    }

    // Тест 1: Вставка в пустой список
    @Test
    public void testInsertIntoEmptyList() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new LinkedListTabulatedFunction(new double[0], new double[0]);
        });
        Assertions.assertEquals("Длина массивов меньше минимальной возможной", exception.getMessage());
    }

    // Тест 2: Обновление существующего X (должен обновить Y)
    @Test
    public void testInsertExistingX() {
        LinkedListTabulatedFunction function = createTestFunction();

        // Проверяем начальное состояние
        assertEquals(20.0, function.getY(1)); // y при x=2.0

        // Вставляем новое значение для существующего x
        function.insert(2.0, 25.0);

        // Проверяем, что значение обновилось
        assertEquals(25.0, function.getY(1));
        // Проверяем, что размер не изменился
        assertEquals(3, function.getCount());
    }

    // Тест 3: Вставка в начало (x < leftBound())
    @Test
    public void testInsertAtBeginning() {
        LinkedListTabulatedFunction function = createTestFunction();

        function.insert(0.5, 5.0);

        // Проверяем размер
        assertEquals(4, function.getCount());
        // Проверяем, что новый элемент в начале
        assertEquals(0.5, function.getX(0));
        assertEquals(5.0, function.getY(0));
        // Проверяем, что голова списка обновилась
        assertEquals(0.5, function.leftBound());
        // Проверяем порядок остальных элементов
        assertEquals(1.0, function.getX(1));
        assertEquals(2.0, function.getX(2));
        assertEquals(3.0, function.getX(3));
    }

    // Тест 4: Вставка в конец (x > rightBound())
    @Test
    public void testInsertAtEnd() {
        LinkedListTabulatedFunction function = createTestFunction();

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

    // Тест 5: Вставка в середину
    @Test
    public void testInsertInMiddle() {
        LinkedListTabulatedFunction function = createTestFunction();

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

    // Тест 6: Вставка между вторым и третьим элементом
    @Test
    public void testInsertBetweenSecondAndThird() {
        LinkedListTabulatedFunction function = createTestFunction();

        function.insert(2.5, 25.0);

        assertEquals(4, function.getCount());
        assertEquals(2.5, function.getX(2));
        assertEquals(25.0, function.getY(2));
        assertEquals(3.0, function.getX(3));
    }

    // Тест 7: Вставка нескольких элементов подряд
    @Test
    public void testInsertMultipleElements() {
        LinkedListTabulatedFunction function = createTestFunction();

        function.insert(0.5, 5.0);  // в начало
        function.insert(2.5, 25.0); // в середину
        function.insert(4.0, 40.0); // в конец

        assertEquals(6, function.getCount());

        // Проверяем правильный порядок
        double[] expectedX = {0.5, 1.0, 2.0, 2.5, 3.0, 4.0};
        double[] expectedY = {5.0, 10.0, 20.0, 25.0, 30.0, 40.0};

        for (int i = 0; i < expectedX.length; i++) {
            assertEquals(expectedX[i], function.getX(i), 0.0001);
            assertEquals(expectedY[i], function.getY(i), 0.0001);
        }
    }

    // Тест 8: Вставка элемента с тем же X после вставки других элементов
    @Test
    public void testUpdateAfterInsert() {
        LinkedListTabulatedFunction function = createTestFunction();

        // Вставляем новый элемент
        function.insert(2.5, 25.0);
        assertEquals(25.0, function.getY(2)); // проверяем вставку

        // Обновляем значение для того же X
        function.insert(2.5, 30.0);
        assertEquals(30.0, function.getY(2)); // проверяем обновление
        assertEquals(4, function.getCount()); // размер не должен измениться
    }

    // Тест 9: Вставка элемента с граничным значением
    @Test
    public void testInsertBoundaryValue() {
        LinkedListTabulatedFunction function = createTestFunction();

        // Вставляем элемент, равный leftBound (должен обновиться)
        function.insert(1.0, 15.0);
        assertEquals(15.0, function.getY(0));
        assertEquals(3, function.getCount());

        // Вставляем элемент, равный rightBound (должен обновиться)
        function.insert(3.0, 35.0);
        assertEquals(35.0, function.getY(2));
        assertEquals(3, function.getCount());
    }

    // Тест 10: Проверка целостности связей после вставки в начало
    @Test
    public void testLinksIntegrityAfterInsertAtBeginning() {
        LinkedListTabulatedFunction function = createTestFunction();

        function.insert(0.5, 5.0);

        // Проверяем, что связи установлены правильно
        assertEquals(0.5, function.leftBound());
        assertEquals(3.0, function.rightBound());

        // Проверяем, что можно пройти по всем элементам
        for (int i = 0; i < function.getCount(); i++) {
            assertNotNull(function.getNode(i));
        }
    }

    // Тест 11: Проверка целостности связей после вставки в середину
    @Test
    public void testLinksIntegrityAfterInsertInMiddle() {
        LinkedListTabulatedFunction function = createTestFunction();

        function.insert(1.5, 15.0);

        // Проверяем связи через индексы
        assertEquals(1.0, function.getX(0));
        assertEquals(1.5, function.getX(1));
        assertEquals(2.0, function.getX(2));
        assertEquals(3.0, function.getX(3));

        // Проверяем, что count корректно увеличился
        assertEquals(4, function.getCount());
    }

    // Тест 12: Вставка отрицательных значений
    @Test
    public void testInsertNegativeValues() {
        double[] xValues = {-2.0, -1.0, 0.0};
        double[] yValues = {4.0, 1.0, 0.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        function.insert(-3.0, 9.0); // до начала
        function.insert(-1.5, 2.25); // в середину

        assertEquals(5, function.getCount());
        assertEquals(-3.0, function.getX(0));
        assertEquals(-1.5, function.getX(2));
    }

    // Тест 13: Вставка с одинаковыми Y значениями
    @Test
    public void testInsertWithSameY() {
        LinkedListTabulatedFunction function = createTestFunction();

        function.insert(1.5, 10.0); // тот же Y, что и у первого элемента

        assertEquals(4, function.getCount());
        assertEquals(10.0, function.getY(1));
    }

    // Тест 14: Проверка индексов после вставки
    @Test
    public void testIndexConsistencyAfterInsert() {
        LinkedListTabulatedFunction function = createTestFunction();

        int initialIndex = function.indexOfX(2.0);
        function.insert(1.5, 15.0);
        int newIndex = function.indexOfX(2.0);

        // Индекс элемента x=2.0 должен измениться после вставки перед ним
        assertEquals(1, initialIndex);
        assertEquals(2, newIndex);
    }

    // Тест 15: Множественные операции вставки и обновления
    @Test
    public void testMultipleOperationsConsistency() {
        LinkedListTabulatedFunction function = createTestFunction();

        // Выполняем различные операции вставки
        function.insert(0.0, 0.0);   // начало
        function.insert(1.5, 15.0);  // середина
        function.insert(2.0, 22.0);  // существующий - обновление
        function.insert(4.0, 40.0);  // конец

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
    // Тест 16: Проверка круговых связей после вставки в начало
    @Test
    public void testCircularLinksAfterInsertAtBeginning() {
        LinkedListTabulatedFunction function = createTestFunction();

        LinkedListTabulatedFunction.Node oldHead = function.getNode(0);
        function.insert(0.5, 5.0);

        LinkedListTabulatedFunction.Node newHead = function.getNode(0);
        LinkedListTabulatedFunction.Node lastNode = function.getNode(function.getCount() - 1);

        // Проверяем круговые связи
        assertEquals(newHead, lastNode.next);
        assertEquals(lastNode, newHead.prev);
    }

    // Тест 17: Проверка связей после вставки в конец
    @Test
    public void testLinksAfterInsertAtEnd() {
        LinkedListTabulatedFunction function = createTestFunction();

        LinkedListTabulatedFunction.Node oldLast = function.getNode(function.getCount() - 1);
        function.insert(4.0, 40.0);

        LinkedListTabulatedFunction.Node newLast = function.getNode(function.getCount() - 1);

        // Проверяем связи
        assertEquals(oldLast, newLast.prev);
        assertEquals(function.getNode(0), newLast.next); // circular link
    }
}

class LinkedListTabulatedFunctionTestRemove {

    private LinkedListTabulatedFunction function;

    public LinkedListTabulatedFunctionTestRemove() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0, 5.0};
        double[] yValues = {10.0, 20.0, 30.0, 40.0, 50.0};
        function = new LinkedListTabulatedFunction(xValues, yValues);
    }

    // Тест 1: Удаление из пустого списка
    @Test
    public void testRemoveFromEmptyList() {


        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new LinkedListTabulatedFunction(new double[0], new double[0]);;
        });
        Assertions.assertEquals("Длина массивов меньше минимальной возможной", exception.getMessage());
    }

    // Тест 2: Удаление с некорректным индексом (отрицательный)
    @Test
    public void testRemoveWithNegativeIndex() {
        int initialCount = function.getCount();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            function.remove(-1);;
        });
        Assertions.assertEquals("Неверный индекс для удаления", exception.getMessage());
    }

    // Тест 3: Удаление с некорректным индексом (больше или равен размеру)
    @Test
    public void testRemoveWithIndexOutOfBounds() {
        int initialCount = function.getCount();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            function.remove(5);
        });
        Assertions.assertEquals("Неверный индекс для удаления", exception.getMessage());

        Exception exception2 = assertThrows(IllegalArgumentException.class, () -> {
            function.remove(10);
        });
        Assertions.assertEquals("Неверный индекс для удаления", exception2.getMessage());

    }

    // Тест 4: Удаление первого элемента (index = 0)
    @Test
    public void testRemoveFirstElement() {
        function.remove(0);

        assertEquals(4, function.getCount());
        // Проверяем, что первый элемент теперь второй из исходного списка
        assertEquals(2.0, function.getX(0));
        assertEquals(20.0, function.getY(0));
        // Проверяем порядок остальных элементов
        assertEquals(3.0, function.getX(1));
        assertEquals(4.0, function.getX(2));
        assertEquals(5.0, function.getX(3));
        // Проверяем, что голова списка обновилась
        assertEquals(2.0, function.leftBound());
    }

    // Тест 5: Удаление последнего элемента (index = count-1)
    @Test
    public void testRemoveLastElement() {
        function.remove(4); // последний элемент

        assertEquals(4, function.getCount());
        // Проверяем, что последний элемент теперь предпоследний из исходного списка
        assertEquals(4.0, function.getX(3));
        assertEquals(40.0, function.getY(3));
        // Проверяем, что первые элементы остались без изменений
        assertEquals(1.0, function.getX(0));
        assertEquals(2.0, function.getX(1));
        assertEquals(3.0, function.getX(2));
        // Проверяем правую границу
        assertEquals(4.0, function.rightBound());
    }

    // Тест 6: Удаление элемента из середины
    @Test
    public void testRemoveMiddleElement() {
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

    // Тест 7: Удаление второго элемента (не первого и не последнего)
    @Test
    public void testRemoveSecondElement() {
        function.remove(1); // удаляем элемент с x=2.0

        assertEquals(4, function.getCount());
        assertEquals(1.0, function.getX(0));
        assertEquals(3.0, function.getX(1)); // теперь на позиции 1 элемент с x=3.0
        assertEquals(4.0, function.getX(2));
        assertEquals(5.0, function.getX(3));
    }

    // Тест 8: Удаление предпоследнего элемента
    @Test
    public void testRemoveSecondLastElement() {
        function.remove(3); // удаляем элемент с x=4.0 (предпоследний)

        assertEquals(4, function.getCount());
        assertEquals(1.0, function.getX(0));
        assertEquals(2.0, function.getX(1));
        assertEquals(3.0, function.getX(2));
        assertEquals(5.0, function.getX(3)); // последний остался
    }

    // Тест 9: Удаление единственного элемента
    @Test
    public void testRemoveSingleElement() {
        double[] xValues = {1.0};
        double[] yValues = {10.0};

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new LinkedListTabulatedFunction(xValues, yValues);
        });
        Assertions.assertEquals("Длина массивов меньше минимальной возможной", exception.getMessage());
    }

    // Тест 10: Удаление нескольких элементов подряд
    @Test
    public void testRemoveMultipleElements() {
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

    // Тест 11: Проверка целостности связей после удаления первого элемента
    @Test
    public void testLinksIntegrityAfterRemoveFirst() {
        LinkedListTabulatedFunction.Node oldSecond = function.getNode(1);

        function.remove(0);

        LinkedListTabulatedFunction.Node newFirst = function.getNode(0);
        // Проверяем, что новый первый элемент - это старый второй
        assertEquals(oldSecond, newFirst);
        // Проверяем круговые связи
        assertEquals(newFirst, function.getNode(3).next);
        assertEquals(function.getNode(3), newFirst.prev);
    }

    // Тест 12: Проверка целостности связей после удаления последнего элемента
    @Test
    public void testLinksIntegrityAfterRemoveLast() {
        LinkedListTabulatedFunction.Node oldLast = function.getNode(4);
        LinkedListTabulatedFunction.Node newLastExpected = function.getNode(3);

        function.remove(4);

        LinkedListTabulatedFunction.Node newLast = function.getNode(3);
        // Проверяем, что новый последний элемент - это старый предпоследний
        assertEquals(newLastExpected, newLast);
        // Проверяем круговые связи
        assertEquals(function.getNode(0), newLast.next);
        assertEquals(newLast, function.getNode(0).prev);
    }

    // Тест 13: Удаление и проверка границ функции
    @Test
    public void testBoundsAfterRemove() {
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

    // Тест 14: Удаление элемента и проверка метода indexOfX
    @Test
    public void testIndexOfXAfterRemove() {
        // Проверяем индексы до удаления
        assertEquals(2, function.indexOfX(3.0));

        // Удаляем элемент с x=2.0 (index = 1)
        function.remove(1);

        // Проверяем индексы после удаления
        assertEquals(-1, function.indexOfX(2.0)); // удаленный элемент
        assertEquals(1, function.indexOfX(3.0)); // сместился на позицию влево
        assertEquals(2, function.indexOfX(4.0)); // сместился на позицию влево
    }

    // Тест 15: Последовательное удаление всех элементов
    @Test
    public void testRemoveAllElementsSequentially() {
        double[] xValues = {1.0, 2.0};
        double[] yValues = {10.0, 20.0};
        LinkedListTabulatedFunction smallFunction = new LinkedListTabulatedFunction(xValues, yValues);

        smallFunction.remove(0);
        assertEquals(1, smallFunction.getCount());
        assertEquals(2.0, smallFunction.getX(0));

        smallFunction.remove(0);
        assertEquals(0, smallFunction.getCount());

        //список пуст
        assertEquals(null, smallFunction.head);
    }

    // Тест 16: Удаление с последующими операциями вставки
    @Test
    public void testInsertAfterRemove() {
        function.remove(2); // удаляем элемент с x=3.0

        // Вставляем новый элемент
        function.insert(2.5, 25.0);

        assertEquals(5, function.getCount());
        // Проверяем порядок
        assertEquals(1.0, function.getX(0));
        assertEquals(2.0, function.getX(1));
        assertEquals(2.5, function.getX(2));
        assertEquals(4.0, function.getX(3));
        assertEquals(5.0, function.getX(4));
    }
}