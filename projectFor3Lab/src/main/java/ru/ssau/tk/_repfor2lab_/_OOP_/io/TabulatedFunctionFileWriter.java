package ru.ssau.tk._repfor2lab_._OOP_.io;

import ru.ssau.tk._repfor2lab_._OOP_.functions.ArrayTabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.LinkedListTabulatedFunction;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class TabulatedFunctionFileWriter {
    public static void main(String[] args) throws IOException {
        try (BufferedWriter fileWriter1 = new BufferedWriter(new FileWriter("output/array function.txt"));
             BufferedWriter fileWriter2 = new BufferedWriter(new FileWriter("output/linked list function.txt"))) {

            double[] xValues1 = {0.00, 0.20, 0.40, 0.60, 0.80};
            double[] yValues1 = {1.00, 1.179, 1.310, 1.390, 1.414};

            ArrayTabulatedFunction function1 = new ArrayTabulatedFunction(xValues1, yValues1);

            double[] xValues2 = {2.00, 2.10, 2.20, 2.40, 2.50};
            double[] yValues2 = {0.135, 0.122, 0.111, 0.091, 0.082};

            LinkedListTabulatedFunction function2 = new LinkedListTabulatedFunction(xValues2, yValues2);

            FunctionsIO.writeTabulatedFunction(fileWriter1, function1);
            FunctionsIO.writeTabulatedFunction(fileWriter2,function2);
        }
        catch (IOException e){
            e.printStackTrace();
        };
    }
}
