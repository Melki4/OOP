package ru.ssau.tk._repfor2lab_._OOP_.functions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.ssau.tk._repfor2lab_._OOP_.exceptions.ArrayIsNotSortedException;
import ru.ssau.tk._repfor2lab_._OOP_.exceptions.DifferentLengthOfArraysException;

import static org.junit.jupiter.api.Assertions.*;

class MockTabulatedFunctionTest {
    @Test
    void MockTest(){

        double[] xValues = {0.2, 0.6, 0.4, 0.8, 0.9};
        double[] yValues = {1.221, 1.492, 1.822, 2.226};

        Exception exception = assertThrows(DifferentLengthOfArraysException.class, () -> {
            AbstractTabulatedFunction.checkLengthIsTheSame(xValues, yValues);
        });
        Assertions.assertEquals("Массивы разной длины", exception.getMessage());

        Exception exceptionWow = assertThrows(ArrayIsNotSortedException.class, () -> {
            AbstractTabulatedFunction.checkSorted(xValues);
        });
        Assertions.assertEquals("Массив не отсортирован", exceptionWow.getMessage());
    }

}