package ru.ssau.tk._repfor2lab_._OOP_.operations;

import org.junit.jupiter.api.Test;
import ru.ssau.tk._repfor2lab_._OOP_.functions.ArrayTabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.MathFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.TabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.factory.ArrayTabulatedFunctionFactory;

import static org.junit.jupiter.api.Assertions.*;

class TabulatedSimpsonMethodTest {

    @Test
    void integrate() {
        TabulatedSimpsonMethod operator = new TabulatedSimpsonMethod(new ArrayTabulatedFunctionFactory());
        MathFunction functions = (double x) -> (x*x + 2*x +1);
        TabulatedFunction f = new ArrayTabulatedFunction(functions, 1, 10, 10000);

        TabulatedFunction result = operator.integrate(f);

        assertEquals(441, result.getY(9999), 0.1);

//        System.out.println(result.getY(9999));
    }
}