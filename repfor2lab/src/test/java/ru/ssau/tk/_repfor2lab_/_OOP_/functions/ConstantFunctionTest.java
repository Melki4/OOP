package ru.ssau.tk._repfor2lab_._OOP_.functions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConstantFunctionTest {
@Test
    void ConstantFTest(){

    assertEquals(4.0, new ConstantFunction(4.0).apply(13));
    assertEquals(4.1, new ConstantFunction(4.1).apply(-32123123));

    var F = new ConstantFunction(4.6);

    assertEquals(F.get_constant(), F.apply(11));
}
}