package ru.ssau.tk._repfor2lab_._OOP_.operations;

import org.junit.jupiter.api.Test;
import ru.ssau.tk._repfor2lab_._OOP_.functions.MathFunction;

import static org.junit.jupiter.api.Assertions.*;

class RightSteppingDifferentialOperatorTest {

    @Test
    void derive() {
        MathFunction functions = (double x) -> (13*x*x + 2*x +1);
        LeftSteppingDifferentialOperator der = new LeftSteppingDifferentialOperator(0.00001);
        MathFunction someNewFunc = der.derive(functions);
        assertEquals(28.0, someNewFunc.apply(1.0), 0.001);
        assertEquals(-24.0, someNewFunc.apply(-1.0), 0.001);
    }
}