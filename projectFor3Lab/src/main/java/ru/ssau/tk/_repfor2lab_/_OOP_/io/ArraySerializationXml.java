package ru.ssau.tk._repfor2lab_._OOP_.io;

import ru.ssau.tk._repfor2lab_._OOP_.functions.ArrayTabulatedFunction;

import java.io.*;

public class ArraySerializationXml {
    public static void main(String[] args) throws IOException {
        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter("output/serialized array functions.xml"))) {

            double[] xValues1 = {0.00, 0.20, 0.40, 0.60, 0.80};
            double[] yValues1 = {1.00, 1.179, 1.310, 1.390, 1.414};

            ArrayTabulatedFunction function1 = new ArrayTabulatedFunction(xValues1, yValues1);

            FunctionsIO.serializeXml(fileWriter, function1);

            fileWriter.close();

            BufferedReader fileReader1 = new BufferedReader(new FileReader("output/serialized array functions.xml"));

            ArrayTabulatedFunction f = FunctionsIO.deserializeXml(fileReader1);

            System.out.println(function1.toString());
            System.out.println(f.toString());

            fileReader1.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
