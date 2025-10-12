package ru.ssau.tk._repfor2lab_._OOP_.io;

import ru.ssau.tk._repfor2lab_._OOP_.functions.TabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.factory.ArrayTabulatedFunctionFactory;
import ru.ssau.tk._repfor2lab_._OOP_.functions.factory.LinkedListTabulatedFunctionFactory;
import ru.ssau.tk._repfor2lab_._OOP_.functions.factory.TabulatedFunctionFactory;
import ru.ssau.tk._repfor2lab_._OOP_.operations.TabulatedDifferentialOperator;

import java.io.*;

public class TabulatedFunctionFileInputStream {
    public static void main(String[] args) {
        try (BufferedInputStream input1 = new BufferedInputStream(new FileInputStream("input/binary function.bin"))) {
            TabulatedFunctionFactory arrayFactory = new ArrayTabulatedFunctionFactory();
            TabulatedFunction fileFunc = FunctionsIO.readTabulatedFunction(input1, arrayFactory);
            System.out.println(fileFunc.toString());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        try {
            System.out.println("Введите размер и значения функции");
            InputStreamReader inputReader = new InputStreamReader(System.in);
            BufferedReader input2 = new BufferedReader(inputReader);
            TabulatedFunctionFactory linkedListFactory = new LinkedListTabulatedFunctionFactory();
            TabulatedFunction consoleFunc = FunctionsIO.readTabulatedFunction(input2, linkedListFactory);

            TabulatedDifferentialOperator difOperator = new TabulatedDifferentialOperator();
            TabulatedFunction derivative = difOperator.derive(consoleFunc);
            System.out.println(derivative.toString());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
