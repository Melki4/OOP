package ru.ssau.tk._repfor2lab_._OOP_.operations;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import ru.ssau.tk._repfor2lab_._OOP_.functions.ArrayTabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.MathFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.TabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.factory.ArrayTabulatedFunctionFactory;

import static org.junit.jupiter.api.Assertions.*;

class TabulatedSimpsonMethodTest {

    private static final MathFunction SIN_FUNCTION = Math::sin;
    private static final MathFunction EXP_FUNCTION = Math::exp;

    @Test
    void integrate() {
        TabulatedSimpsonMethod operator = new TabulatedSimpsonMethod(new ArrayTabulatedFunctionFactory());
        MathFunction functions = (double x) -> (x*x + 2*x +1);
        TabulatedFunction f = new ArrayTabulatedFunction(functions, 1, 10, 10000);

        TabulatedFunction result = operator.integrate(f);

        assertEquals(441, result.getY(9999), 0.1);

//        System.out.println(result.getY(9999));
    }

    @Test
    @Timeout(5)
    void testSinFunctionIntegration() {
        // ∫ sin(x) dx от 0 до π = [-cos(x)] от 0 до π = (-cos(π)) - (-cos(0)) = (1) - (-1) = 2
        TabulatedSimpsonMethod operator = new TabulatedSimpsonMethod(new ArrayTabulatedFunctionFactory());
        TabulatedFunction f = new ArrayTabulatedFunction(SIN_FUNCTION, 0, Math.PI, 10001);
        double expected = 2.0;
//        System.out.println(f);

        TabulatedFunction result = operator.integrate(f);

        assertEquals(expected, result.getY(10000), 0.01, "Интеграл синуса");
    }

    @Test
    @Timeout(5)
    void testExpFunctionIntegration() {
        // ∫ e^x dx от 0 до 1 = [e^x] от 0 до 1 = e - 1 ≈ 1.71828
        TabulatedSimpsonMethod operator = new TabulatedSimpsonMethod(new ArrayTabulatedFunctionFactory());
        TabulatedFunction f = new ArrayTabulatedFunction(EXP_FUNCTION, 0, 10, 1001);
        double expected = Math.E - 1;

        TabulatedFunction result = operator.integrate(f);

        assertEquals(expected, result.getY(1000), 0.01, "Интеграл экспоненты");
    }
}