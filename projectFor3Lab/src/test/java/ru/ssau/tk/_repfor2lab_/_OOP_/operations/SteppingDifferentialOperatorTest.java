package ru.ssau.tk._repfor2lab_._OOP_.operations;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SteppingDifferentialOperatorTest {

    @Test
    public void testSteppingDifferentialOperatorConstructor() {
        // Test valid step
        SteppingDifferentialOperator operator = new LeftSteppingDifferentialOperator(0.1);
        assertEquals(0.1, operator.getStep(), 1e-9);

        // Test setter with valid value
        operator.setStep(0.2);
        assertEquals(0.2, operator.getStep(), 1e-9);
    }

    @Test
    public void testConstructorWithZeroStep() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new LeftSteppingDifferentialOperator(0.0);
        });
        Assertions.assertEquals("Неверный аргумент в диф. операторе", exception.getMessage());
    }

    @Test
    public void testConstructorWithNegativeStep() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new LeftSteppingDifferentialOperator(-1.0);
        });
        Assertions.assertEquals("Неверный аргумент в диф. операторе", exception.getMessage());
    }

    @Test
    public void testConstructorWithPositiveInfinity() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new LeftSteppingDifferentialOperator(Double.POSITIVE_INFINITY);
        });
        Assertions.assertEquals("Неверный аргумент в диф. операторе", exception.getMessage());
    }

    @Test
    public void testConstructorWithNegativeInfinity() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new LeftSteppingDifferentialOperator(Double.NEGATIVE_INFINITY);
        });
        Assertions.assertEquals("Неверный аргумент в диф. операторе", exception.getMessage());
    }

    @Test
    public void testConstructorWithNaN() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
        new LeftSteppingDifferentialOperator(Double.NaN);
    });
        Assertions.assertEquals("Неверный аргумент в диф. операторе", exception.getMessage());
    }

    @Test
    public void testSetterWithInvalidValue() {
        SteppingDifferentialOperator operator = new LeftSteppingDifferentialOperator(0.1);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            operator.setStep(-0.1);
        });
        Assertions.assertEquals("Неверный аргумент в диф. операторе", exception.getMessage());
    }
}