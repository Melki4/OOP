package ru.ssau.tk._repfor2lab_._OOP_.functions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IdentityFunctionTest {
    @Test
    void XEualsX(){//вернуть должен аргумент
        var func = new IdentityFunction();
        assertEquals(5.99, func.apply(5.99));
        assertEquals(4, func.apply(4));
        assertEquals(0, func.apply(0));
        assertEquals(-3, func.apply(-3));
    }
}