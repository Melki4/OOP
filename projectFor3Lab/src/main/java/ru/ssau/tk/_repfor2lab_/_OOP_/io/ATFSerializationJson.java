package ru.ssau.tk._repfor2lab_._OOP_.io;

import ru.ssau.tk._repfor2lab_._OOP_.functions.ArrayTabulatedFunction;

import java.io.*;

public class ATFSerializationJson {
    public static void main(String[] args) throws IOException {
        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter("output/serialized array functions.json"))) {

            double[] xValues1 = {0.00, 0.20, 0.40, 0.60, 0.80, 1.0};
            double[] yValues1 = {1.00, 1.179, 1.310, 1.390, 1.414, 1.674};

            ArrayTabulatedFunction function1 = new ArrayTabulatedFunction(xValues1, yValues1);

            FunctionsIO.serializeJson(fileWriter, function1);

            fileWriter.close();

            BufferedReader fileReader1 = new BufferedReader(new FileReader("output/serialized array functions.json"));

            ArrayTabulatedFunction f = FunctionsIO.deserializeJson(fileReader1);

            System.out.println(function1);
            System.out.println(f);

            fileReader1.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
