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
        ArrayTabulatedFunction arrayTabulatedFunction = new ArrayTabulatedFunction(func, 1, 10, 20);

        list = new SynchronizedTabulatedFunction(listTabulatedFunction);
        array = new SynchronizedTabulatedFunction(arrayTabulatedFunction);
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

        MathFunction func = (double x) -> x*x -2*x+1;

        TabulatedFunction f = new ArrayTabulatedFunction(func, 1, 10, 10);

        TabulatedFunction derivative = operatorArray.deriveSynchronously(f);

        assertEquals(f.getCount(), derivative.getCount());

        assertEquals(1.0, derivative.getY(0), 1e-9);
        assertEquals(3.0, derivative.getY(1), 1e-9);
        assertEquals(5.0, derivative.getY(2), 1e-9);
        assertEquals(7.0, derivative.getY(3), 1e-9);
        assertEquals(9.0, derivative.getY(4), 1e-9);
        assertEquals(11.0, derivative.getY(5), 1e-9);
        assertEquals(13.0, derivative.getY(6), 1e-9);
        assertEquals(15.0, derivative.getY(7), 1e-9);
        assertEquals(17.0, derivative.getY(8), 1e-9);
        assertEquals(17.0, derivative.getY(9), 1e-9);
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