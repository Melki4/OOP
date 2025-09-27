package ru.ssau.tk._repfor2lab_._OOP_.functions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MathFunctionTest {
    @Test
    void MathFTest(){
        MathFunction f = new IdentityFunction();
        MathFunction g = new SqrFunction();
        MathFunction h = new IdentityFunction();
        var composite = f.andThen(g).andThen(h);
        assertEquals(169.0, composite.apply(13.0));
        assertEquals(256.0, new SqrFunction().andThen(g).andThen(new CompositeFunction(new IdentityFunction(), new SqrFunction())).apply(2));
        assertEquals(625.0, new SqrFunction().andThen(new SqrFunction().andThen(new ConstantFunction(5.0))).apply(3));
        assertEquals(6.0, new IdentityFunction().andThen(new ConstantFunction(6.0)).apply(-45.0));
    }

}