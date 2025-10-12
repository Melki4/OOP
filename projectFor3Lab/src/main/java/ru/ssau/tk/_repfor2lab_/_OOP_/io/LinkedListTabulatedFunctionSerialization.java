package ru.ssau.tk._repfor2lab_._OOP_.io;

import ru.ssau.tk._repfor2lab_._OOP_.functions.LinkedListTabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.TabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.operations.TabulatedDifferentialOperator;

import java.io.*;

public class LinkedListTabulatedFunctionSerialization {
    public static void main(String[] args) throws IOException {
        try (BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream("output/serialized linked list functions.bin"))) {
            double[] xValues = {1.0, 2.0, 3.0, 4.0, 5.0};
            double[] yValues = {1.0, 4.0, 9.0, 16.0, 25.0};
            LinkedListTabulatedFunction func = new LinkedListTabulatedFunction(xValues, yValues);

            TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();
            TabulatedFunction firstDerivative = operator.derive(func);
            TabulatedFunction secondDerivative = operator.derive(firstDerivative);

            FunctionsIO.serialize(writer, func);
            FunctionsIO.serialize(writer, firstDerivative);
            FunctionsIO.serialize(writer, secondDerivative);
            writer.close();

            BufferedInputStream reader = new BufferedInputStream(new FileInputStream("output/serialized linked list functions.bin"));
            TabulatedFunction deserealized_func = FunctionsIO.deserialize(reader);
            TabulatedFunction deserealized_firDerivative = FunctionsIO.deserialize(reader);
            TabulatedFunction deserealized_secDerivative = FunctionsIO.deserialize(reader);

            System.out.println(deserealized_func.toString());
            System.out.println(deserealized_firDerivative.toString());
            System.out.println(deserealized_secDerivative.toString());
        }
        catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
