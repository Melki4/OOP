package ru.ssau.tk._repfor2lab_._OOP_.operations;

import org.junit.jupiter.api.Test;
import ru.ssau.tk._repfor2lab_._OOP_.functions.MathFunction;

import static org.junit.jupiter.api.Assertions.*;

class MiddleSteppingDifferentialOperatorTest {

    @Test
    void derive() {
        MathFunction functions = (double x) -> (x*x*x + 2*x*x +x);
        MiddleSteppingDifferentialOperator der = new MiddleSteppingDifferentialOperator(0.00001);
        MathFunction someNewFunc = der.derive(functions);
        assertEquals(8.0, someNewFunc.apply(1.0), 0.001);
        assertEquals(0.0, someNewFunc.apply(-1.0), 0.001);
    }
}