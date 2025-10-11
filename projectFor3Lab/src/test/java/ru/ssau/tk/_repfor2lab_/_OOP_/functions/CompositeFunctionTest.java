package ru.ssau.tk._repfor2lab_._OOP_.functions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

class CompositeFunctionTest {

    @Test
    void CompTest(){

        var f1 = new IdentityFunction();
        var f2 = new SqrFunction();
        var f3 = new CompositeFunction(f1, f2);

        assertEquals(16.0, f3.apply(4));
        assertEquals(36.0, new CompositeFunction(f2, f1).apply(6));
        assertEquals(1296.0, new CompositeFunction(new CompositeFunction (new IdentityFunction(), new SqrFunction()), new SqrFunction()).apply(6));

        MathFunction sqr =  x -> x * x;
        MathFunction linear =  x -> 2 * x + 1;

        var fHard = new CompositeFunction(linear, sqr);

        assertEquals(9.0, fHard.apply(1));
    }
}