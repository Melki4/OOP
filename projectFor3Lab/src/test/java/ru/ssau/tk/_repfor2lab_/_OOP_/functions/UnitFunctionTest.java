package ru.ssau.tk._repfor2lab_._OOP_.functions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UnitFunctionTest {
@Test
    void UnitFTest(){
    var test = new UnitFunction();
    assertEquals(1.0, test.apply(32332));
    assertEquals(1.0, test.apply(33333));
    assertEquals(1.0, test.apply(-43434));
}
}