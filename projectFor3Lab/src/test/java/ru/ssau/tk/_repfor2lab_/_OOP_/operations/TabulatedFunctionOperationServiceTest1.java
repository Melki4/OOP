package ru.ssau.tk._repfor2lab_._OOP_.operations;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.ssau.tk._repfor2lab_._OOP_.exceptions.DifferentLengthOfArraysException;
import ru.ssau.tk._repfor2lab_._OOP_.exceptions.InconsistentFunctionsException;
import ru.ssau.tk._repfor2lab_._OOP_.functions.factory.ArrayTabulatedFunctionFactory;
import ru.ssau.tk._repfor2lab_._OOP_.functions.factory.LinkedListTabulatedFunctionFactory;
import ru.ssau.tk._repfor2lab_._OOP_.functions.factory.TabulatedFunctionFactory;
import ru.ssau.tk._repfor2lab_._OOP_.functions.*;

import static org.junit.jupiter.api.Assertions.*;

class TabulatedFunctionOperationServiceTest1 {

    @Test
    public void testConstructorWithFactory() {
        TabulatedFunctionFactory factory = new LinkedListTabulatedFunctionFactory();
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService(factory);
        assertSame(service.get(), factory);
    }

    @Test
    public void testDefaultConstructor() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();

        assertInstanceOf(ArrayTabulatedFunctionFactory.class, service.get());
    }

    @Test
    public void testGetSetFactory() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();
        TabulatedFunctionFactory originalFactory = service.get();

        TabulatedFunctionFactory newFactory = new LinkedListTabulatedFunctionFactory();
        service.set(newFactory);

        assertSame(service.get(), newFactory);
        assertNotSame(service.get(), originalFactory);
    }

    @Test
    public void testAsPoints() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {4.0, 5.0, 6.0};
        TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        Point[] points = TabulatedFunctionOperationService.asPoints(function);

        assertEquals(3, points.length);
        assertEquals(1.0, points[0].x);
        assertEquals(4.0, points[0].y);
        assertEquals(2.0, points[1].x);
        assertEquals(5.0, points[1].y);
        assertEquals(3.0, points[2].x);
        assertEquals(6.0, points[2].y);
    }

    @Test
    public void testAdditionSameType() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();

        double[] xValues1 = {1.0, 2.0, 3.0};
        double[] yValues1 = {1.0, 2.0, 3.0};
        TabulatedFunction function1 = new ArrayTabulatedFunction(xValues1, yValues1);

        double[] xValues2 = {1.0, 2.0, 3.0};
        double[] yValues2 = {4.0, 5.0, 6.0};
        TabulatedFunction function2 = new ArrayTabulatedFunction(xValues2, yValues2);

        TabulatedFunction result = service.addition(function1, function2);

        assertEquals(3, result.getCount());
        assertEquals(1.0, result.getX(0));
        assertEquals(5.0, result.getY(0));
        assertEquals(2.0, result.getX(1));
        assertEquals(7.0, result.getY(1));
        assertEquals(3.0, result.getX(2));
        assertEquals(9.0, result.getY(2));
    }

    @Test
    public void testAdditionDifferentTypes() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();

        // ArrayTabulatedFunction + LinkedListTabulatedFunction
        double[] xValues1 = {1.0, 2.0, 3.0};
        double[] yValues1 = {2.0, 3.0, 4.0};
        TabulatedFunction function1 = new ArrayTabulatedFunction(xValues1, yValues1);

        double[] xValues2 = {1.0, 2.0, 3.0};
        double[] yValues2 = {1.0, 1.0, 1.0};
        TabulatedFunction function2 = new LinkedListTabulatedFunction(xValues2, yValues2);

        TabulatedFunction result = service.addition(function1, function2);

        assertEquals(3, result.getCount());
        assertEquals(3.0, result.getY(0));
        assertEquals(4.0, result.getY(1));
        assertEquals(5.0, result.getY(2));
    }

    @Test
    public void testSubtractionSameType() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();

        double[] xValues1 = {1.0, 2.0, 3.0};
        double[] yValues1 = {5.0, 6.0, 7.0};
        TabulatedFunction function1 = new ArrayTabulatedFunction(xValues1, yValues1);

        double[] xValues2 = {1.0, 2.0, 3.0};
        double[] yValues2 = {1.0, 2.0, 3.0};
        TabulatedFunction function2 = new ArrayTabulatedFunction(xValues2, yValues2);

        TabulatedFunction result = service.subtraction(function1, function2);

        assertEquals(3, result.getCount());
        assertEquals(4.0, result.getY(0));
        assertEquals(4.0, result.getY(1));
        assertEquals(4.0, result.getY(2));
    }

    @Test
    public void testSubtractionDifferentTypes() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();

        // ArrayTabulatedFunction - LinkedListTabulatedFunction
        double[] xValues1 = {1.0, 2.0, 3.0};
        double[] yValues1 = {10.0, 10.0, 10.0};
        TabulatedFunction function1 = new ArrayTabulatedFunction(xValues1, yValues1);

        double[] xValues2 = {1.0, 2.0, 3.0};
        double[] yValues2 = {3.0, 4.0, 5.0};
        TabulatedFunction function2 = new LinkedListTabulatedFunction(xValues2, yValues2);

        TabulatedFunction result = service.subtraction(function1, function2);

        assertEquals(3, result.getCount());
        assertEquals(7.0, result.getY(0));
        assertEquals(6.0, result.getY(1));
        assertEquals(5.0, result.getY(2));
    }

    @Test
    public void testAdditionWithLinkedListFactory() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService(new LinkedListTabulatedFunctionFactory());

        double[] xValues1 = {1.0, 2.0, 3.0};
        double[] yValues1 = {1.0, 2.0, 3.0};
        TabulatedFunction function1 = new ArrayTabulatedFunction(xValues1, yValues1);

        double[] xValues2 = {1.0, 2.0, 3.0};
        double[] yValues2 = {2.0, 3.0, 4.0};
        TabulatedFunction function2 = new LinkedListTabulatedFunction(xValues2, yValues2);

        TabulatedFunction result = service.addition(function1, function2);

        // Проверяем, что фабрика создала правильный тип функции
        assertInstanceOf(LinkedListTabulatedFunction.class, result);
        assertEquals(3.0, result.getY(0));
        assertEquals(5.0, result.getY(1));
        assertEquals(7.0, result.getY(2));
    }

    @Test
    public void testSubtractionWithArrayFactory() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService(new ArrayTabulatedFunctionFactory());

        double[] xValues1 = {1.0, 2.0, 3.0};
        double[] yValues1 = {8.0, 9.0, 10.0};
        TabulatedFunction function1 = new LinkedListTabulatedFunction(xValues1, yValues1);

        double[] xValues2 = {1.0, 2.0, 3.0};
        double[] yValues2 = {1.0, 2.0, 3.0};
        TabulatedFunction function2 = new ArrayTabulatedFunction(xValues2, yValues2);

        TabulatedFunction result = service.subtraction(function1, function2);

        // Проверяем, что фабрика создала правильный тип функции
        assertTrue(result instanceof ArrayTabulatedFunction);
        assertEquals(7.0, result.getY(0));
        assertEquals(7.0, result.getY(1));
        assertEquals(7.0, result.getY(2));
    }

    @Test
    public void testAdditionWithDifferentCount() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();

        double[] xValues1 = {1.0, 2.0, 3.0};
        double[] yValues1 = {1.0, 2.0, 3.0};
        TabulatedFunction function1 = new ArrayTabulatedFunction(xValues1, yValues1);

        double[] xValues2 = {1.0, 2.0}; // разное количество точек
        double[] yValues2 = {4.0, 5.0};
        TabulatedFunction function2 = new ArrayTabulatedFunction(xValues2, yValues2);

        Exception exception = assertThrows(InconsistentFunctionsException.class, () -> {
            service.addition(function1, function2);
        });
        Assertions.assertEquals("Разное кол-во элементов в функциях", exception.getMessage());
    }

    @Test
    public void testSubtractionWithDifferentXValues() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();

        double[] xValues1 = {1.0, 2.0, 3.0};
        double[] yValues1 = {1.0, 2.0, 3.0};
        TabulatedFunction function1 = new ArrayTabulatedFunction(xValues1, yValues1);

        double[] xValues2 = {1.0, 2.5, 3.0}; // разные значения X
        double[] yValues2 = {4.0, 5.0, 6.0};
        TabulatedFunction function2 = new ArrayTabulatedFunction(xValues2, yValues2);


        Exception exception = assertThrows(InconsistentFunctionsException.class, () -> {
            service.subtraction(function1, function2);
        });
        Assertions.assertEquals("Разные элементы икс в массивах", exception.getMessage());
    }

    @Test
    public void testOperationsWithNegativeValues() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();

        double[] xValues1 = {1.0, 2.0, 3.0};
        double[] yValues1 = {-1.0, -2.0, -3.0};
        TabulatedFunction function1 = new ArrayTabulatedFunction(xValues1, yValues1);

        double[] xValues2 = {1.0, 2.0, 3.0};
        double[] yValues2 = {2.0, 1.0, 0.0};
        TabulatedFunction function2 = new LinkedListTabulatedFunction(xValues2, yValues2);

        TabulatedFunction additionResult = service.addition(function1, function2);
        assertEquals(1.0, additionResult.getY(0));
        assertEquals(-1.0, additionResult.getY(1));
        assertEquals(-3.0, additionResult.getY(2));

        TabulatedFunction subtractionResult = service.subtraction(function1, function2);
        assertEquals(-3.0, subtractionResult.getY(0));
        assertEquals(-3.0, subtractionResult.getY(1));
        assertEquals(-3.0, subtractionResult.getY(2));
    }
    @Test
    public void testMultiplicationWithNegativeValues() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();

        double[] xValues1 = {1.0, 2.0, 3.0};
        double[] yValues1 = {-1.0, -2.0, -3.0};
        TabulatedFunction function1 = new ArrayTabulatedFunction(xValues1, yValues1);

        double[] xValues2 = {1.0, 2.0, 3.0};
        double[] yValues2 = {2.0, 1.0, 0.0};
        TabulatedFunction function2 = new LinkedListTabulatedFunction(xValues2, yValues2);

        TabulatedFunction multiplicationResult = service.multiplication(function1, function2);
        assertEquals(-2.0, multiplicationResult.getY(0));
        assertEquals(-2.0, multiplicationResult.getY(1));
        assertEquals(-0.0, multiplicationResult.getY(2));
    }

    @Test
    public void testDivisionWithNegativeValues() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();

        double[] xValues1 = {1.0, 2.0, 3.0};
        double[] yValues1 = {-4.0, -6.0, -8.0};
        TabulatedFunction function1 = new ArrayTabulatedFunction(xValues1, yValues1);

        double[] xValues2 = {1.0, 2.0, 3.0};
        double[] yValues2 = {2.0, 3.0, 4.0};
        TabulatedFunction function2 = new LinkedListTabulatedFunction(xValues2, yValues2);

        TabulatedFunction divisionResult = service.division(function1, function2);
        assertEquals(-2.0, divisionResult.getY(0));
        assertEquals(-2.0, divisionResult.getY(1));
        assertEquals(-2.0, divisionResult.getY(2));
    }

    @Test
    public void testMultiplicationWithMixedTypes() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();

        double[] xValues1 = {1.0, 2.0, 3.0, 4.0};
        double[] yValues1 = {2.0, 3.0, 4.0, 5.0};
        TabulatedFunction function1 = new ArrayTabulatedFunction(xValues1, yValues1);

        double[] xValues2 = {1.0, 2.0, 3.0, 4.0};
        double[] yValues2 = {-1.0, -2.0, -3.0, -4.0};
        TabulatedFunction function2 = new LinkedListTabulatedFunction(xValues2, yValues2);

        TabulatedFunction multiplicationResult = service.multiplication(function1, function2);
        assertEquals(-2.0, multiplicationResult.getY(0));
        assertEquals(-6.0, multiplicationResult.getY(1));
        assertEquals(-12.0, multiplicationResult.getY(2));
        assertEquals(-20.0, multiplicationResult.getY(3));
    }

    @Test
    public void testDivisionWithMixedTypes() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();

        double[] xValues1 = {1.0, 2.0, 3.0, 4.0};
        double[] yValues1 = {6.0, 8.0, 10.0, 12.0};
        TabulatedFunction function1 = new ArrayTabulatedFunction(xValues1, yValues1);

        double[] xValues2 = {1.0, 2.0, 3.0, 4.0};
        double[] yValues2 = {2.0, 4.0, 5.0, 6.0};
        TabulatedFunction function2 = new LinkedListTabulatedFunction(xValues2, yValues2);

        TabulatedFunction divisionResult = service.division(function1, function2);
        assertEquals(3.0, divisionResult.getY(0));
        assertEquals(2.0, divisionResult.getY(1));
        assertEquals(2.0, divisionResult.getY(2));
        assertEquals(2.0, divisionResult.getY(3));
    }

    @Test
    public void testMultiplicationWithZero() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();

        double[] xValues1 = {1.0, 2.0, 3.0};
        double[] yValues1 = {5.0, 10.0, 15.0};
        TabulatedFunction function1 = new ArrayTabulatedFunction(xValues1, yValues1);

        double[] xValues2 = {1.0, 2.0, 3.0};
        double[] yValues2 = {0.0, 0.0, 0.0};
        TabulatedFunction function2 = new LinkedListTabulatedFunction(xValues2, yValues2);

        TabulatedFunction multiplicationResult = service.multiplication(function1, function2);
        assertEquals(0.0, multiplicationResult.getY(0));
        assertEquals(0.0, multiplicationResult.getY(1));
        assertEquals(0.0, multiplicationResult.getY(2));
    }

    @Test
    public void testDivisionByOne() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();

        double[] xValues1 = {1.0, 2.0, 3.0};
        double[] yValues1 = {-3.0, -6.0, -9.0};
        TabulatedFunction function1 = new ArrayTabulatedFunction(xValues1, yValues1);

        double[] xValues2 = {1.0, 2.0, 3.0};
        double[] yValues2 = {1.0, 1.0, 1.0};
        TabulatedFunction function2 = new LinkedListTabulatedFunction(xValues2, yValues2);

        TabulatedFunction divisionResult = service.division(function1, function2);
        assertEquals(-3.0, divisionResult.getY(0));
        assertEquals(-6.0, divisionResult.getY(1));
        assertEquals(-9.0, divisionResult.getY(2));
    }

}