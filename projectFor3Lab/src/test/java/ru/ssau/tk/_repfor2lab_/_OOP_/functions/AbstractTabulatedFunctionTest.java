package ru.ssau.tk._repfor2lab_._OOP_.functions;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class AbstractTabulatedFunctionTest {
    @Test
    public void testArrayTabulatedFunctionToString() {
        double[] xValues = {0.0, 0.5, 1.0};
        double[] yValues = {0.0, 0.25, 1.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        String expected = "ArrayTabulatedFunction size = 3\n[0.0; 0.0]\n[0.5; 0.25]\n[1.0; 1.0]";
        assertEquals(expected, function.toString());
    }

    @Test
    public void testLinkedListTabulatedFunctionToString() {
        double[] xValues = {0.0, 0.5, 1.0};
        double[] yValues = {0.0, 0.25, 1.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        String expected = "LinkedListTabulatedFunction size = 3\n[0.0; 0.0]\n[0.5; 0.25]\n[1.0; 1.0]";
        assertEquals(expected, function.toString());
    }

    @Test
    public void testToStringWithSinglePoint() {
        double[] xValues = {1.0};
        double[] yValues = {2.0};

        double[] tempX = {1.0, 2.0};
        double[] tempY = {2.0, 4.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(tempX, tempY);
        function.remove(1);

        String expected = "LinkedListTabulatedFunction size = 1\n[1.0; 2.0]";
        assertEquals(expected, function.toString());
    }

    @Test
    public void testToStringWithMultiplePoints() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {1.0, 4.0, 9.0, 16.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        String expected = "ArrayTabulatedFunction size = 4\n[1.0; 1.0]\n[2.0; 4.0]\n[3.0; 9.0]\n[4.0; 16.0]";
        assertEquals(expected, function.toString());
    }

    @Test
    public void testToStringWithNegativeValues() {
        double[] xValues = {-2.0, -1.0, 0.0, 1.0, 2.0};
        double[] yValues = {4.0, 1.0, 0.0, 1.0, 4.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        String expected = "LinkedListTabulatedFunction size = 5\n[-2.0; 4.0]\n[-1.0; 1.0]\n[0.0; 0.0]\n[1.0; 1.0]\n[2.0; 4.0]";
        assertEquals(expected, function.toString());
    }

    @Test
    public void testToStringWithDecimalValues() {
        double[] xValues = {0.1, 0.2, 0.3};
        double[] yValues = {0.01, 0.04, 0.09};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        String expected = "ArrayTabulatedFunction size = 3\n[0.1; 0.01]\n[0.2; 0.04]\n[0.3; 0.09]";
        assertEquals(expected, function.toString());
    }

    @Test
    public void testToStringWithLargeNumbers() {
        double[] xValues = {1000.0, 2000.0, 3000.0};
        double[] yValues = {1000000.0, 4000000.0, 9000000.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        String expected = "LinkedListTabulatedFunction size = 3\n[1000.0; 1000000.0]\n[2000.0; 4000000.0]\n[3000.0; 9000000.0]";
        assertEquals(expected, function.toString());
    }

    @Test
    public void testToStringFormatConsistency() {
        double[] xValues = {1.5, 2.5, 3.5};
        double[] yValues = {2.25, 6.25, 12.25};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        String result = function.toString();

        assertTrue(result.startsWith("ArrayTabulatedFunction size = 3"));
        assertTrue(result.contains("\n[1.5; 2.25]"));
        assertTrue(result.contains("\n[2.5; 6.25]"));
        assertTrue(result.contains("\n[3.5; 12.25]"));
        assertEquals(4, result.split("\n").length); // заголовок + 3 точки
    }

    @Test
    public void testEmptyFunctionToString() {
        // Создаем функцию с минимальным количеством точек и удаляем все
        double[] tempX = {1.0, 2.0};
        double[] tempY = {1.0, 4.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(tempX, tempY);
        function.remove(1);
        function.remove(0);

        String expected = "LinkedListTabulatedFunction size = 0";
        assertEquals(expected, function.toString());
    }
}