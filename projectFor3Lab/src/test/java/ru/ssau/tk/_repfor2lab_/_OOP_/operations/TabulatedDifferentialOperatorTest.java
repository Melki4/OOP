package ru.ssau.tk._repfor2lab_._OOP_.operations;
import org.junit.jupiter.api.Test;
import ru.ssau.tk._repfor2lab_._OOP_.functions.ArrayTabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.LinkedListTabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.TabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.factory.ArrayTabulatedFunctionFactory;
import ru.ssau.tk._repfor2lab_._OOP_.functions.factory.LinkedListTabulatedFunctionFactory;

import static org.junit.jupiter.api.Assertions.*;

class TabulatedDifferentialOperatorTest {
    @Test
    public void testDeriveConstantFunctionWithArrayFactory() {
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator(new ArrayTabulatedFunctionFactory());

        // f(x) = 5 (константная функция)
        double[] xValues = {0.0, 1.0, 2.0, 3.0, 4.0};
        double[] yValues = {5.0, 5.0, 5.0, 5.0, 5.0};

        TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);
        TabulatedFunction derivative = operator.derive(function);

        assertTrue(derivative instanceof ArrayTabulatedFunction);

        // Производная константной функции должна быть 0
        for (int i = 0; i < derivative.getCount(); i++) {
            assertEquals(0.0, derivative.getY(i), 0.0001, "Производная константы должна быть 0 в точке " + i);
        }
    }

    @Test
    public void testDeriveConstantFunctionWithLinkedListFactory() {
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator(new LinkedListTabulatedFunctionFactory());

        // f(x) = 3 (константная функция)
        double[] xValues = {-2.0, -1.0, 0.0, 1.0, 2.0};
        double[] yValues = {3.0, 3.0, 3.0, 3.0, 3.0};

        TabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);
        TabulatedFunction derivative = operator.derive(function);

        assertTrue(derivative instanceof LinkedListTabulatedFunction);

        // Производная константной функции должна быть 0
        for (int i = 0; i < derivative.getCount(); i++) {
            assertEquals(0.0, derivative.getY(i), 0.0001, "Производная константы должна быть 0 в точке " + i);
        }
    }

    @Test
    public void testDeriveLinearFunctionWithArrayFactory() {
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator(new ArrayTabulatedFunctionFactory());

        // f(x) = 2x + 1 (линейная функция)
        double[] xValues = {0.0, 1.0, 2.0, 3.0, 4.0};
        double[] yValues = {1.0, 3.0, 5.0, 7.0, 9.0};

        TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);
        TabulatedFunction derivative = operator.derive(function);

        assertTrue(derivative instanceof ArrayTabulatedFunction);

        // Производная линейной функции f(x) = 2x + 1 должна быть 2
        for (int i = 0; i < derivative.getCount(); i++) {
            assertEquals(2.0, derivative.getY(i), 0.0001, "Производная линейной функции должна быть постоянной в точке " + i);
        }
    }

    @Test
    public void testDeriveLinearFunctionWithLinkedListFactory() {
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator(new LinkedListTabulatedFunctionFactory());

        // f(x) = -0.5x + 2 (линейная функция с отрицательным угловым коэффициентом)
        double[] xValues = {0.0, 2.0, 4.0, 6.0, 8.0};
        double[] yValues = {2.0, 1.0, 0.0, -1.0, -2.0};

        TabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);
        TabulatedFunction derivative = operator.derive(function);

        assertTrue(derivative instanceof LinkedListTabulatedFunction);

        // Производная линейной функции f(x) = -0.5x + 2 должна быть -0.5
        for (int i = 0; i < derivative.getCount(); i++) {
            assertEquals(-0.5, derivative.getY(i), 0.0001, "Производная должна быть -0.5 в точке " + i);
        }
    }

    @Test
    public void testDeriveQuadraticFunctionWithArrayFactory() {
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator(new ArrayTabulatedFunctionFactory());

        // f(x) = x^2 (квадратичная функция)
        double[] xValues = {0.0, 1.0, 2.0, 3.0, 4.0};
        double[] yValues = {0.0, 1.0, 4.0, 9.0, 16.0};

        TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);
        TabulatedFunction derivative = operator.derive(function);

        assertTrue(derivative instanceof ArrayTabulatedFunction);

        // Теоретическая производная = 2x
        // Численная производная будет приближенной
        assertEquals(1.0, derivative.getY(0), 0.0001);  // ~2*0.5 = 1.0
        assertEquals(3.0, derivative.getY(1), 0.0001);  // ~2*1.5 = 3.0
        assertEquals(5.0, derivative.getY(2), 0.0001);  // ~2*2.5 = 5.0
        assertEquals(7.0, derivative.getY(3), 0.0001);  // ~2*3.5 = 7.0
        assertEquals(7.0, derivative.getY(4), 0.0001);  // последнее равно предпоследнему
    }

    @Test
    public void testDeriveQuadraticFunctionWithLinkedListFactory() {
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator(new LinkedListTabulatedFunctionFactory());

        // f(x) = x^2 - 2x + 1
        double[] xValues = {-1.0, 0.0, 1.0, 2.0, 3.0};
        double[] yValues = {4.0, 1.0, 0.0, 1.0, 4.0};

        TabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);
        TabulatedFunction derivative = operator.derive(function);

        assertTrue(derivative instanceof LinkedListTabulatedFunction);

        // Теоретическая производная = 2x - 2
        assertEquals(-3.0, derivative.getY(0), 0.0001);  // ~2*(-0.5) - 2 = -3.0
        assertEquals(-1.0, derivative.getY(1), 0.0001);  // ~2*(0.5) - 2 = -1.0
        assertEquals(1.0, derivative.getY(2), 0.0001);   // ~2*(1.5) - 2 = 1.0
        assertEquals(3.0, derivative.getY(3), 0.0001);   // ~2*(2.5) - 2 = 3.0
        assertEquals(3.0, derivative.getY(4), 0.0001);   // последнее равно предпоследнему
    }

    @Test
    public void testDeriveWithDifferentStepSizes() {
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator(new ArrayTabulatedFunctionFactory());

        // Тестируем с разным шагом сетки
        // f(x) = 3x (линейная функция)
        double[] xValues1 = {0.0, 1.0, 2.0};                    // шаг = 1
        double[] yValues1 = {0.0, 3.0, 6.0};

        double[] xValues2 = {0.0, 0.5, 1.0, 1.5, 2.0};         // шаг = 0.5
        double[] yValues2 = {0.0, 1.5, 3.0, 4.5, 6.0};

        TabulatedFunction function1 = new ArrayTabulatedFunction(xValues1, yValues1);
        TabulatedFunction function2 = new ArrayTabulatedFunction(xValues2, yValues2);

        TabulatedFunction derivative1 = operator.derive(function1);
        TabulatedFunction derivative2 = operator.derive(function2);

        // Обе производные должны быть постоянными и равными 3
        for (int i = 0; i < derivative1.getCount() - 1; i++) {
            assertEquals(3.0, derivative1.getY(i), 0.0001);
        }

        for (int i = 0; i < derivative2.getCount() - 1; i++) {
            assertEquals(3.0, derivative2.getY(i), 0.0001);
        }
    }

    @Test
    public void testDeriveWithMinimumPoints() {
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator(new LinkedListTabulatedFunctionFactory());

        // Минимальное количество точек (2)
        double[] xValues = {1.0, 2.0};
        double[] yValues = {1.0, 3.0}; // f(x) = 2x - 1

        TabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);
        TabulatedFunction derivative = operator.derive(function);

        assertTrue(derivative instanceof LinkedListTabulatedFunction);
        assertEquals(2, derivative.getCount());
        assertEquals(2.0, derivative.getY(0), 0.0001); // (3-1)/(2-1) = 2
        assertEquals(2.0, derivative.getY(1), 0.0001); // последнее равно предпоследнему
    }

    @Test
    public void testDeriveWithFactorySwitching() {
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();

        // Начально используем фабрику по умолчанию (Array)
        double[] xValues = {0.0, 1.0, 2.0};
        double[] yValues = {0.0, 1.0, 4.0};

        TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);
        TabulatedFunction derivative1 = operator.derive(function);
        assertTrue(derivative1 instanceof ArrayTabulatedFunction);

        // Меняем фабрику на LinkedList
        operator.setFactory(new LinkedListTabulatedFunctionFactory());
        TabulatedFunction derivative2 = operator.derive(function);
        assertTrue(derivative2 instanceof LinkedListTabulatedFunction);

        // Проверяем, что значения производных одинаковы (разная реализация, но одинаковые данные)
        assertEquals(derivative1.getCount(), derivative2.getCount());
        for (int i = 0; i < derivative1.getCount(); i++) {
            assertEquals(derivative1.getX(i), derivative2.getX(i), 0.0001);
            assertEquals(derivative1.getY(i), derivative2.getY(i), 0.0001);
        }
    }

    @Test
    public void testDeriveWithNonUniformGrid() {
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator(new ArrayTabulatedFunctionFactory());

        // Неравномерная сетка
        double[] xValues = {0.0, 1.0, 3.0, 6.0, 10.0};
        double[] yValues = {0.0, 2.0, 6.0, 12.0, 20.0}; // f(x) = x^2 + x

        TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);
        TabulatedFunction derivative = operator.derive(function);

        // Проверяем вычисления с разными шагами
        assertEquals(2.0, derivative.getY(0), 0.0001);  // (2-0)/(1-0) = 2
        assertEquals(2.0, derivative.getY(1), 0.0001);  // (6-2)/(3-1) = 4/2 = 2
        assertEquals(2.0, derivative.getY(2), 0.0001);  // (12-6)/(6-3) = 6/3 = 2
        assertEquals(2.0, derivative.getY(3), 0.0001);  // (20-12)/(10-6) = 8/4 = 2
        assertEquals(2.0, derivative.getY(4), 0.0001);  // последнее равно предпоследнему
    }

    @Test
    public void testDerivePreservesXValues() {
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator(new LinkedListTabulatedFunctionFactory());

        double[] xValues = {0.5, 1.5, 2.5, 3.5, 4.5};
        double[] yValues = {1.0, 2.0, 3.0, 4.0, 5.0};

        TabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);
        TabulatedFunction derivative = operator.derive(function);

        // X-значения должны сохраниться
        for (int i = 0; i < derivative.getCount(); i++) {
            assertEquals(derivative.getX(i), xValues[i], 0.0001,
                    "X-значения должны сохраняться при дифференцировании в точке " + i);
        }
    }
}