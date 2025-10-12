package ru.ssau.tk._repfor2lab_._OOP_.io;

import ru.ssau.tk._repfor2lab_._OOP_.functions.ArrayTabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.LinkedListTabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.TabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.factory.ArrayTabulatedFunctionFactory;
import ru.ssau.tk._repfor2lab_._OOP_.functions.factory.LinkedListTabulatedFunctionFactory;
import ru.ssau.tk._repfor2lab_._OOP_.operations.TabulatedDifferentialOperator;

import java.io.*;

public class ArrayTabulatedFunctionSerialization {
    public static void main(String[] args) throws IOException {
        try (BufferedOutputStream fileWriter = new BufferedOutputStream(new FileOutputStream("output/serialized array functions.bin"))) {

            TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator(new ArrayTabulatedFunctionFactory());

            double[] xValues = {0.00, 1.00, 2.00, 3.00, 4.00, 5.00, 6.00, 7.00, 8.00, 9.00};
            double[] yValues = {0.00, 1.60, 3.40, 5.40, 7.60, 10.00, 12.60, 15.40, 18.40, 21.60};

            TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);
            TabulatedFunction derivative1 = operator.derive(function);
            TabulatedFunction derivative2 = operator.derive(derivative1);

            FunctionsIO.serialize(fileWriter, function);
            FunctionsIO.serialize(fileWriter, derivative1);
            FunctionsIO.serialize(fileWriter, derivative2);

            fileWriter.close();

            BufferedInputStream fileReader = new BufferedInputStream(new FileInputStream("output/serialized array functions.bin"));

            TabulatedFunction deserealized_function = FunctionsIO.deserialize(fileReader);
            TabulatedFunction deserealized_derivative1 = FunctionsIO.deserialize(fileReader);
            TabulatedFunction deserealized_derivative2 = FunctionsIO.deserialize(fileReader);

            System.out.println(deserealized_function.toString());
            System.out.println(deserealized_derivative1.toString());
            System.out.println(deserealized_derivative2.toString());

        }
        catch (IOException e){
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
