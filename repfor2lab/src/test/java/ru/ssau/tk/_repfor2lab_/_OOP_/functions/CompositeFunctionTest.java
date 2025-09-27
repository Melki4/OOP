package ru.ssau.tk._repfor2lab_._OOP_.functions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CompositeFunctionTest {
    @Test
    void CompTest(){
        var f1 = new IdentityFunction();
        var f2 = new SqrFunction();
        var f3 = new CompositeFunction(f1, f2);
        assertEquals(16, f3.apply(4));
        assertEquals(36, new CompositeFunction(f2, f1).apply(6));
        assertEquals(1296, new CompositeFunction(new CompositeFunction (new IdentityFunction(), new SqrFunction()), new SqrFunction()).apply(6));
    }
}