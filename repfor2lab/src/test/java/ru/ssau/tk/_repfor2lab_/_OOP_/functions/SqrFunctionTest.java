package ru.ssau.tk._repfor2lab_._OOP_.functions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SqrFunctionTest {
    @Test
    void EqualsToSqr(){
        var sqr = new SqrFunction();
        assertEquals(16.0, sqr.apply(4.0));
    }
}