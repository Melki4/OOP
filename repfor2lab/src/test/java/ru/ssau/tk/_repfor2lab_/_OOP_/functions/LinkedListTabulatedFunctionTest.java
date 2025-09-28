package ru.ssau.tk._repfor2lab_._OOP_.functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

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
        assertEquals(0, function.floorIndexOfX(0.5)); // Левее левой границы
        assertEquals(3, function.floorIndexOfX(6.0));  // Правее правой границы
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
        double result = function.interpolate(2.5);
        double expected = 4.0 + (2.5 - 2.0) / (3.0 - 2.0) * (9.0 - 4.0);
        assertEquals(expected, result, 1e-10);
    }

    @Test
    void testSingleNodeFunction() {
        // Тест функции с одной точкой
        double[] xValues = {5.0};
        double[] yValues = {10.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        assertEquals(1, function.getCount());
        assertEquals(5.0, function.leftBound());
        assertEquals(5.0, function.rightBound());
        assertEquals(10.0, function.getY(0));

        // Экстраполяция для функции с одной точкой
        assertEquals(10.0, function.extrapolateLeft(0.0));
        assertEquals(10.0, function.extrapolateRight(10.0));
        assertEquals(10.0, function.interpolate(5.0));
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

    @Test
    void testEdgeCases() {
        // Граничные случаи
        double[] xValues = {0.0};
        double[] yValues = {0.0};
        LinkedListTabulatedFunction singlePoint = new LinkedListTabulatedFunction(xValues, yValues);

        assertEquals(0.0, singlePoint.interpolate(100.0)); // Всегда возвращает одно значение

        // Функция с одинаковыми x
        MathFunction constant = x -> 7.0;
        LinkedListTabulatedFunction constantFunc = new LinkedListTabulatedFunction(constant, 5.0, 5.0, 10);
        assertEquals(7.0, constantFunc.getY(9));
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
        MathFunction sumFunction = x -> func1.interpolate(x) + func2.interpolate(x);

        ArrayTabulatedFunction sumTabulated = new ArrayTabulatedFunction(sumFunction, 0.0, 3.0, 10);

        // Проверяем значения суммы
        assertEquals(1.0, sumTabulated.interpolate(0.0), 0.1);  // 0 + 1 = 1
        assertEquals(4.0, sumTabulated.interpolate(1.0), 0.1);  // 1 + 3 = 4
        assertEquals(9.0, sumTabulated.interpolate(2.0), 0.1);  // 4 + 5 = 9
        assertEquals(16.0, sumTabulated.interpolate(3.0), 0.1); // 9 + 7 = 16
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
            double gx = g_linkedList.interpolate(x);
            return f_array.interpolate(gx);
        };

        ArrayTabulatedFunction composed = new ArrayTabulatedFunction(composition, -1.0, 3.0, 20);

        // Проверяем значения композиции
        assertEquals(-1.0, composed.interpolate(-1.0), 0.1);  // f(g(-1)) = f(-1) = -1.0 (экстраполяция)
        assertEquals(1.6, composed.interpolate(0.1), 0.1);    // f(g(0)) = f(1) = 1
        assertEquals(10.4, composed.interpolate(1.1), 0.1);    // f(g(1)) = f(3) = 9
    }

    @Test
    void testConstantFunctionCombinations() {
        // Постоянная функция в комбинациях
        ArrayTabulatedFunction constantArray = new ArrayTabulatedFunction(constant, 0.0, 5.0, 10);
        LinkedListTabulatedFunction linearLinkedList = new LinkedListTabulatedFunction(linear, 0.0, 5.0, 10);

        // Сумма постоянной и линейной функции
        MathFunction sumWithConstant = x -> constantArray.interpolate(x) + linearLinkedList.interpolate(x);
        ArrayTabulatedFunction sumTabulated = new ArrayTabulatedFunction(sumWithConstant, 0.0, 5.0, 15);

        // Проверяем: 5 + (2x + 1) = 2x + 6
        assertEquals(6.0, sumTabulated.interpolate(0.0), 0.1);  // 2*0 + 6 = 6
        assertEquals(8.0, sumTabulated.interpolate(1.0), 0.1);  // 2*1 + 6 = 8
        assertEquals(16.0, sumTabulated.interpolate(5.0), 0.1); // 2*5 + 6 = 16
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
                arrayFunc.interpolate(x) * linkedFunc.interpolate(x);

        // Проверяем экстраполяцию слева
        double leftExtrapolationArray = arrayFunc.interpolate(0.0);
        double leftExtrapolationLinked = linkedFunc.interpolate(0.0);
        assertEquals(leftExtrapolationArray * leftExtrapolationLinked,
                extrapolationTest.apply(0.0), 0.1);

        // Проверяем экстраполяцию справа
        double rightExtrapolationArray = arrayFunc.interpolate(4.0);
        double rightExtrapolationLinked = linkedFunc.interpolate(4.0);
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

    @Test
    void testEdgeCaseCombinations() {
        // Тестируем граничные случаи в комбинациях
        double[] singlePointX = {0.0};
        double[] singlePointY = {1.0};

        ArrayTabulatedFunction singleArray = new ArrayTabulatedFunction(singlePointX, singlePointY);
        LinkedListTabulatedFunction singleLinkedList = new LinkedListTabulatedFunction(singlePointX, singlePointY);

        // Комбинация двух функций с одной точкой
        MathFunction singleCombo = x -> singleArray.interpolate(x) + singleLinkedList.interpolate(x);
        ArrayTabulatedFunction singleComboTabulated = new ArrayTabulatedFunction(singleCombo, -1.0, 1.0, 5);

        // Все значения должны быть 2.0 (1 + 1)
        for (int i = 0; i < singleComboTabulated.getCount(); i++) {
            assertEquals(2.0, singleComboTabulated.getY(i), 1e-9);
        }
    }
}