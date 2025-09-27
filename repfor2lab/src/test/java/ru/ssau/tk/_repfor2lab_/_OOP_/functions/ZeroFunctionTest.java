package ru.ssau.tk._repfor2lab_._OOP_.functions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ZeroFunctionTest {
@Test
    void ZeroFTest() {
        var test = new ZeroFunction();
        assertEquals(0.0, test.apply(545.545));
        assertEquals(0.0, test.apply(3));
        assertEquals(0.0, test.apply(0.0000));
    }
}