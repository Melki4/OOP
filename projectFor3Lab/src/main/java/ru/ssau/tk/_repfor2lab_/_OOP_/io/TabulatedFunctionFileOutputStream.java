package ru.ssau.tk._repfor2lab_._OOP_.io;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import ru.ssau.tk._repfor2lab_._OOP_.functions.ArrayTabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.LinkedListTabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.TabulatedFunction;
import java.io.IOException;

public class TabulatedFunctionFileOutputStream {
    public static void main(String[] args) throws IOException {
        try (FileOutputStream arrayStream = new FileOutputStream("output/array function.bin");
             BufferedOutputStream bufferedArrayStream = new BufferedOutputStream(arrayStream);
             FileOutputStream linkedListStream = new FileOutputStream("output/linked list function.bin");
             BufferedOutputStream bufferedLinkedListStream = new BufferedOutputStream(linkedListStream)){
            double[] xValues = {1.0, 2.0, 3.0, 4.0, 5.0};
            double[] yValues = {1.0, 4.0, 9.0, 16.0, 25.0};
            TabulatedFunction arrayFunc = new ArrayTabulatedFunction(xValues, yValues);
            TabulatedFunction linkedListFunc = new LinkedListTabulatedFunction(xValues, yValues);
            FunctionsIO.writeTabulatedFunction(bufferedArrayStream, arrayFunc);
            FunctionsIO.writeTabulatedFunction(bufferedLinkedListStream, linkedListFunc);
        }
        catch (IOException e) {
            e.printStackTrace();
        };
    }
}
