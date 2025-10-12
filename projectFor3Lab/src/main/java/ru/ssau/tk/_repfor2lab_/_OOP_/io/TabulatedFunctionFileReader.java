package ru.ssau.tk._repfor2lab_._OOP_.io;

import ru.ssau.tk._repfor2lab_._OOP_.functions.ArrayTabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.factory.ArrayTabulatedFunctionFactory;
import ru.ssau.tk._repfor2lab_._OOP_.functions.factory.LinkedListTabulatedFunctionFactory;
import ru.ssau.tk._repfor2lab_._OOP_.functions.factory.TabulatedFunctionFactory;

import java.io.*;
import java.text.ParseException;

public class TabulatedFunctionFileReader {
    public static void main(String[] args){

        try (BufferedReader fileReader1 = new BufferedReader(new FileReader("input/function.txt"));
             BufferedReader fileReader2 = new BufferedReader(new FileReader("input/function.txt"))) {

            System.out.println(FunctionsIO.readTabulatedFunction(fileReader1, new ArrayTabulatedFunctionFactory()).toString());

            System.out.println(FunctionsIO.readTabulatedFunction(fileReader2, new LinkedListTabulatedFunctionFactory()).toString());
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
