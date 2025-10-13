package ru.ssau.tk._repfor2lab_._OOP_.functions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.ssau.tk._repfor2lab_._OOP_.exceptions.InterpolationException;

import static org.junit.jupiter.api.Assertions.*;

class LinkedListTabulatedFunctionExtraTest {
    @Test
    void LinkedListTest(){
        double[] xValues = {1.0, 2.3, 13.6};
        double[] yValues = {5.0};

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new LinkedListTabulatedFunction(xValues, yValues);
        });
        Assertions.assertEquals("Длина массивов меньше минимальной возможной", exception.getMessage());
    }
    @Test
    void LinkedList1(){
        MathFunction function = (double x) -> (x*x+2*x-13);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new LinkedListTabulatedFunction(function, 7, 15, 1);
        });
        Assertions.assertEquals("Количество элементов меньше минимума", exception.getMessage());
    }
    @Test
    void LinkedList2(){
        MathFunction function = (double x) -> (x*x+2*x-13);

        LinkedListTabulatedFunction source = new LinkedListTabulatedFunction(function, 7, 15, 2);
        source.remove(0);
        source.remove(0);

        source.insert(1.3, 2.6);

        source.remove(0);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            source.remove(0);
        });
        Assertions.assertEquals("Неверный индекс для удаления", exception.getMessage());
    }
    @Test
    void LinkedList3(){
        MathFunction function = (double x) -> (x*x+2*x-13);

        LinkedListTabulatedFunction source = new LinkedListTabulatedFunction(function, 7, 15, 2);
        source.remove(0);
        source.remove(0);

        source.insert(1.3, 2.6);

        source.remove(0);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            source.getNode(0);
        });
        Assertions.assertEquals("Неверный индекс для получения узла", exception.getMessage());
    }
    @Test
    void LinkedList4(){
        double[] xValues = {1.0, 2.3};
        double[] yValues = {5.0, 3.4};

        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        function.remove(1);

        Exception exception = assertThrows(InterpolationException.class, () -> {
            function.extrapolateRight(2.0);
        });
        Assertions.assertEquals("Мало аргументов для экстраполяции", exception.getMessage());
    }
    @Test
    void LinkedList5(){
        double[] xValues = {1.0, 2.3};
        double[] yValues = {5.0, 3.4};

        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        function.remove(1);

        Exception exception = assertThrows(InterpolationException.class, () -> {
            function.extrapolateLeft(2.0);
        });
        Assertions.assertEquals("Мало аргументов для экстраполяции", exception.getMessage());
    }
    @Test
    void LinkedList6(){
        double[] xValues = {1.0, 2.3};
        double[] yValues = {5.0, 3.4};

        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        function.remove(1);

        Exception exception = assertThrows(InterpolationException.class, () -> {
            function.interpolate(13.0);
        });

        Assertions.assertEquals("Мало аргументов для интерполяции", exception.getMessage());
    }
    @Test
    void LinkedList7(){
        MathFunction function = (double x) -> (x*x+2*x-13);
        LinkedListTabulatedFunction source =  new LinkedListTabulatedFunction(function, 7, 15, 2);
        source.remove(0);
        source.remove(0);

        Exception exception = assertThrows(NullPointerException.class, () -> {
            source.getY(0);
        });
        Assertions.assertEquals("Обращение к пустому списку", exception.getMessage());
    }
    @Test
    void LinkedList8(){
        MathFunction function = (double x) -> (x*x+2*x-13);
        LinkedListTabulatedFunction source =  new LinkedListTabulatedFunction(function, 7, 15, 2);
        source.remove(0);
        source.remove(0);

        Exception exception = assertThrows(NullPointerException.class, () -> {
            source.setY(0, 45.6);
        });
        Assertions.assertEquals("Обращение к пустому списку", exception.getMessage());
    }
    @Test
    void LinkedList9(){
        MathFunction function = (double x) -> (x*x+2*x-13);

        LinkedListTabulatedFunction source =  new LinkedListTabulatedFunction(function, 7, 15, 2);

        source.remove(0);
        source.remove(0);

        Exception exception = assertThrows(NullPointerException.class, () -> {
            source.getX(0);
        });
        Assertions.assertEquals("Обращение к пустому списку", exception.getMessage());
    }
}