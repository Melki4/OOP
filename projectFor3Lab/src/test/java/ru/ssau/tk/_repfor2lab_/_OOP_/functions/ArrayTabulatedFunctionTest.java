package ru.ssau.tk._repfor2lab_._OOP_.functions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ArrayTabulatedFunctionTest {
    @Test
    void ArrayTest1(){
        double[] xValues = {0.00, 0.20, 0.40, 0.60, 0.80};
        double[] yValues = {1.00, 1.179, 1.310, 1.390, 1.414};
        var TestedVar = new ArrayTabulatedFunction(xValues, yValues);
        assertEquals(1.4088, TestedVar.apply(0.77), 0.1);
    }
    @Test
    void ArrayTest2(){
        double[] xValues = {0.00, 0.20, 0.40, 0.60, 0.80};
        double[] yValues = {0.00, 0.198, 0.388, 0.564, 0.717};
        var TestedVar = new ArrayTabulatedFunction(xValues, yValues);
        assertEquals(0.342, TestedVar.apply(0.35), 0.1);
    }
    @Test
    void ArrayTest3(){
        double[] xValues = {1.00, 1.20, 1.40, 1.60, 1.80};
        double[] yValues = {2.718, 3.320, 4.055, 4.953, 6.050};
        var TestedVar = new ArrayTabulatedFunction(xValues, yValues);
        assertEquals(4.482, TestedVar.apply(1.5), 0.1);
    }
    @Test
    void ArrayTest4(){
        double[] xValues = {2.00, 2.10, 2.20, 2.40, 2.50};
        double[] yValues = {0.135, 0.122, 0.111, 0.091, 0.082};
        var TestedVar = new ArrayTabulatedFunction(xValues, yValues);
        assertEquals(0.100, TestedVar.apply(2.3), 0.1);
    }
    @Test
    void ArrayTest5(){
        double[] xValues = {0.2, 0.4, 0.6, 0.8, 0.9};
        double[] yValues = {1.221, 1.492, 1.822, 2.226, 2.460};
        var TestedVar = new ArrayTabulatedFunction(xValues, yValues);
        assertEquals(2.718, TestedVar.apply(1.0), 0.1);
    }
    @Test
    void ArrayTest6(){
        double[] xValues = {0.0, 0.5, 1.0, 1.5, 2.0};
        double[] yValues = {1.000, 1.649, 2.718, 4.482, 7.389};
        var TestedVar = new ArrayTabulatedFunction(xValues, yValues);
        assertEquals(0.351, TestedVar.apply(-0.5), 0.1);
    }
    @Test
    void ArrayTest7(){
        double[] xValues = {1.0, 2.0, 3.0, 4.0, 5.0};
        double[] yValues = {5.0, 7.0, 9.0, 11.0, 13.0};
        var TestedVar = new ArrayTabulatedFunction(xValues, yValues);
        assertEquals(3.0, TestedVar.apply(0.0), 0.1);
    }
    @Test
    void ArrayTest8(){
        double[] xValues = {1.0};
        double[] yValues = {5.0};

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new ArrayTabulatedFunction(xValues, yValues);
        });
        Assertions.assertEquals("Длина массивов меньше минимальной возможной", exception.getMessage());

    }

    @Test
    void ArrayTest9(){
        double[] xValues = {1.0, 2.0, 3.0, 4.0, 5.0};
        double[] yValues = {5.0, 7.0, 9.0, 11.0, 13.0};
        var TestedVar = new ArrayTabulatedFunction(xValues, yValues);
        assertEquals(11.0, TestedVar.apply(4.0), 0.1);
    }
    @Test
    void ArrayTest10(){
        MathFunction f =  x -> x*x +2*x-1;
        var TestedVar = new ArrayTabulatedFunction(f, 0, 3, 100);
//      for (var i =0;i<100;++i) System.out.println(TestedVar.getX(i) + " " + TestedVar.getY(i));
        assertEquals(-0.56, TestedVar.apply(0.2), 0.1);
    }
    @Test
    void ArrayTest11(){
        MathFunction f = x -> (Math.pow(4, x)-2*x+1);
        var TestedVar = new ArrayTabulatedFunction(f, -5, 3, 100);
        assertNotEquals(548.276, TestedVar.apply(4.56), 0.1);
    }
    @Test
    void ArrayTest12(){
        MathFunction f = x -> -2*x+1;
        var TestedVar = new ArrayTabulatedFunction(f, -5, 3, 100);
        assertEquals(-152.08, TestedVar.apply(76.54), 0.1);
    }
    @Test
    void ArrayTest13() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};
        var TestedVar = new ArrayTabulatedFunction(xValues, yValues);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
           TestedVar.floorIndexOfX(0.0);
        });
        Assertions.assertEquals("Икс меньше левой границы", exception.getMessage());
    }
    @Test
    void ArrayTest14() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new ArrayTabulatedFunction(xValues, yValues).getX(-1);
        });
        Assertions.assertEquals("Индекс выходит за пределы", exception.getMessage());
    }
    @Test
    void ArrayTest15() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new ArrayTabulatedFunction(xValues, yValues).getX(3);
        });
        Assertions.assertEquals("Индекс выходит за пределы", exception.getMessage());
    }
    @Test
    void ArrayTest16() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new ArrayTabulatedFunction(xValues, yValues).getY(-1);
        });
        Assertions.assertEquals("Индекс выходит за пределы", exception.getMessage());
    }
    @Test
    void ArrayTest17() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new ArrayTabulatedFunction(xValues, yValues).getY(5);
        });
        Assertions.assertEquals("Индекс выходит за пределы", exception.getMessage());
    }
    @Test
    void ArrayTest18() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new ArrayTabulatedFunction(xValues, yValues).setY(10, 10.0);
        });
        Assertions.assertEquals("Индекс выходит за пределы", exception.getMessage());
    }
    @Test
    void ArrayTest19() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new ArrayTabulatedFunction(xValues, yValues).setY(-1, 10.0);
        });
        Assertions.assertEquals("Индекс выходит за пределы", exception.getMessage());
    }
}

