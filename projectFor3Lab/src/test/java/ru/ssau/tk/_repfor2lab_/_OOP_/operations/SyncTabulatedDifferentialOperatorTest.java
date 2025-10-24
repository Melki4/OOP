package ru.ssau.tk._repfor2lab_._OOP_.operations;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.ssau.tk._repfor2lab_._OOP_.concurrent.SynchronizedTabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.*;
import ru.ssau.tk._repfor2lab_._OOP_.functions.factory.ArrayTabulatedFunctionFactory;
import ru.ssau.tk._repfor2lab_._OOP_.functions.factory.LinkedListTabulatedFunctionFactory;

import static org.junit.jupiter.api.Assertions.*;

public class SyncTabulatedDifferentialOperatorTest{

    SynchronizedTabulatedFunction list;
    SynchronizedTabulatedFunction array;

    TabulatedDifferentialOperator operatorArray = new TabulatedDifferentialOperator(new ArrayTabulatedFunctionFactory());
    TabulatedDifferentialOperator operatorList = new TabulatedDifferentialOperator(new LinkedListTabulatedFunctionFactory());

    @BeforeEach
    void create(){

        double[] xValues = {1, 2, 3, 4, 5, 6, 7,8, 9, 10};
        double[] yValues = {1.2, 1.3, 1.4, 1.4, 1.6, 1.8, 1.5, 1.5, 1.6, 1.7};

        MathFunction func = (double x) -> x*x -2*x+1;

        LinkedListTabulatedFunction listTabulatedFunction = new LinkedListTabulatedFunction(xValues, yValues);
        ArrayTabulatedFunction arrayTabulatedFunction = new ArrayTabulatedFunction(func, 1, 10, 10000);

        list = new SynchronizedTabulatedFunction(listTabulatedFunction);
        array = new SynchronizedTabulatedFunction(arrayTabulatedFunction);
    }

    @Test
    void testDeriveSynchronously() {
        MiddleSteppingDifferentialOperator operator = new MiddleSteppingDifferentialOperator(0.0001);

        MathFunction derivative = operator.deriveSynchronously(array);

        assertEquals(0.0, derivative.apply(1.0), 1e-3);
        assertEquals(18.0, derivative.apply(10.0), 1e-3);
    }

    @Test
    void testDeriveSynchronously1() {

        LeftSteppingDifferentialOperator operator = new LeftSteppingDifferentialOperator(0.0001);

        MathFunction derivative = operator.deriveSynchronously(array);

        assertEquals(0.0, derivative.apply(1.0), 1e-3);
        assertEquals(8.0, derivative.apply(5.0), 1e-3);
    }

    @Test
    void testDeriveSynchronously2() {
        RightSteppingDifferentialOperator operator = new RightSteppingDifferentialOperator(0.0001);

        MathFunction derivative = operator.deriveSynchronously(array);

        assertEquals(0.0, derivative.apply(1.0), 1e-3);
        assertEquals(12.0, derivative.apply(7.0), 1e-3);
    }

    @Test
    void testDeriveSynchronouslyWithSynchronizedTabulatedFunction() {
        TabulatedFunction derivative = operatorList.deriveSynchronously(list);

        assertEquals(list.getCount(), derivative.getCount());

        assertEquals(0.1, derivative.getY(0), 1e-9);
        assertEquals(0.1, derivative.getY(1), 1e-9);
        assertEquals(0.0, derivative.getY(2), 1e-9);
        assertEquals(0.2, derivative.getY(3), 1e-9);
        assertEquals(0.2, derivative.getY(4), 1e-9);
    }

    @Test
    void testDeriveSynchronouslyWithRegularTabulatedFunction() {

        TabulatedFunction derivative = operatorArray.deriveSynchronously(array);

        assertEquals(array.getCount(), derivative.getCount());

        assertEquals(0.0, derivative.getY(0), 1e-2);
        assertEquals(0.0, derivative.getY(1), 1e-2);
        assertEquals(0.0, derivative.getY(2), 1e-2);
    }

    @Test
    void testDeriveSynchronouslyWithLinearFunction() {
        double[] xValues = {0.0, 1.0, 2.0, 3.0, 4.0};
        double[] yValues = {1.0, 3.0, 5.0, 7.0, 9.0};
        TabulatedFunction func = new LinkedListTabulatedFunction(xValues, yValues);

        TabulatedFunction derivative = operatorList.deriveSynchronously(func);

        assertEquals(2.0, derivative.getY(0), 1e-9);
        assertEquals(2.0, derivative.getY(1), 1e-9);
        assertEquals(2.0, derivative.getY(2), 1e-9);
        assertEquals(2.0, derivative.getY(3), 1e-9);
        assertEquals(2.0, derivative.getY(4), 1e-9);
    }

   @Test
    void testDeriveSynchronouslyWithConstantFunction() {
        // Тестирование с константной функцией f(x) = 5
        double[] xValues = {-2.0, -1.0, 0.0, 1.0, 2.0};
        double[] yValues = {5.0, 5.0, 5.0, 5.0, 5.0};
        TabulatedFunction func = new ArrayTabulatedFunction(xValues, yValues);

        TabulatedFunction derivative = operatorArray.deriveSynchronously(func);

        for (int i = 0; i < derivative.getCount(); i++) {
            assertEquals(0.0, derivative.getY(i));
        }
    }
}