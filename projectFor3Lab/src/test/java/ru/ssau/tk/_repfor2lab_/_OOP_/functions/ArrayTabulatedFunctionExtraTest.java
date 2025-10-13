package ru.ssau.tk._repfor2lab_._OOP_.functions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.ssau.tk._repfor2lab_._OOP_.exceptions.InterpolationException;

import static org.junit.jupiter.api.Assertions.*;

class ArrayTabulatedFunctionExtraTest {
    @Test
    void ArrayTest(){
        double[] xValues = {1.0, 2.3};
        double[] yValues = {5.0, 3.4};

        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        function.remove(1);

        Exception exception = assertThrows(InterpolationException.class, () -> {
            function.interpolate(2.0);
        });
        Assertions.assertEquals("Мало аргументов для интерполяции", exception.getMessage());
    }
    @Test
    void ArrayTest2(){
        double[] xValues = {1.0, 2.3};
        double[] yValues = {5.0, 3.4};

        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        function.remove(1);

        Exception exception = assertThrows(InterpolationException.class, () -> {
            function.extrapolateRight(2.0);
        });
        Assertions.assertEquals("Мало аргументов для экстраполяции", exception.getMessage());
    }
    @Test
    void ArrayTest3(){
        double[] xValues = {1.0, 2.3};
        double[] yValues = {5.0, 3.4};

        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        function.remove(1);

        Exception exception = assertThrows(InterpolationException.class, () -> {
            function.extrapolateLeft(2.0);
        });
        Assertions.assertEquals("Мало аргументов для экстраполяции", exception.getMessage());
    }
    @Test
    void ArrayTest4(){
        double[] xValues = {1.0, 2.3};
        double[] yValues = {5.0, 3.4};

        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        function.remove(0);
        function.remove(0);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            function.remove(0);
        });
        Assertions.assertEquals("Неверный индекс для удаления", exception.getMessage());
    }
    @Test
    void ArrayTest5(){
        double[] xValues = {1.0};
        double[] yValues = {5.0};

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new ArrayTabulatedFunction(xValues, yValues);
        });
        Assertions.assertEquals("Длина массивов меньше минимальной возможной", exception.getMessage());
    }
    @Test
    void ArrayTest6(){
        MathFunction function = (double x) -> (x*x+2*x-13);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new ArrayTabulatedFunction(function, 7, 15, 1);
        });
        Assertions.assertEquals("Количество элементов меньше минимума", exception.getMessage());
    }
    @Test
    void ArrayTest7(){
        MathFunction function = (double x) -> (x*x+2*x-13);
        ArrayTabulatedFunction source =  new ArrayTabulatedFunction(function, 7, 15, 2);
        source.remove(0);
        source.remove(0);

        Exception exception = assertThrows(NullPointerException.class, () -> {
            source.getY(0);
        });
        Assertions.assertEquals("Обращение к пустому массиву", exception.getMessage());
    }
    @Test
    void ArrayTest8(){
        MathFunction function = (double x) -> (x*x+2*x-13);
        ArrayTabulatedFunction source =  new ArrayTabulatedFunction(function, 7, 15, 2);
        source.remove(0);
        source.remove(0);

        Exception exception = assertThrows(NullPointerException.class, () -> {
            source.setY(0, 45.6);
        });
        Assertions.assertEquals("Обращение к пустому массиву", exception.getMessage());
    }
}
