package ru.ssau.tk._repfor2lab_._OOP_.io;

import org.junit.AfterClass;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import ru.ssau.tk._repfor2lab_._OOP_.functions.ArrayTabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.LinkedListTabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.TabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.factory.ArrayTabulatedFunctionFactory;
import ru.ssau.tk._repfor2lab_._OOP_.functions.factory.LinkedListTabulatedFunctionFactory;
import ru.ssau.tk._repfor2lab_._OOP_.operations.TabulatedDifferentialOperator;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FunctionsIOTest {
    @AfterAll
    public static void cleaning(){
         Path path = Paths.get("путь/к/папке"); // Замените на ваш путь
         try {
             File folder = new File("D:\\GitProject\\oopprod\\projectFor3Lab\\temp");
             for (File file : folder.listFiles())
             {
                Files.delete(file.toPath());
             }

         } catch (Exception e) {
             System.out.println("Не удалось удалить директорию: " + e.getMessage());
         }
    }
    @Test
    void test1(){
        try (BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream("temp/serialized linked list functions.bin"))) {
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

            BufferedInputStream reader = new BufferedInputStream(new FileInputStream("temp/serialized linked list functions.bin"));
            TabulatedFunction deserealized_func = FunctionsIO.deserialize(reader);
            TabulatedFunction deserealized_firDerivative = FunctionsIO.deserialize(reader);
            TabulatedFunction deserealized_secDerivative = FunctionsIO.deserialize(reader);

            assertEquals(func.toString(), deserealized_func.toString());
            assertEquals(firstDerivative.toString(), deserealized_firDerivative.toString());
            assertEquals(secondDerivative.toString(), deserealized_secDerivative.toString());

            reader.close();
        }
        catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    @Test
    void test2(){
        try (BufferedWriter fileWriter1 = new BufferedWriter(new FileWriter("temp/array function.txt"));
             BufferedWriter fileWriter2 = new BufferedWriter(new FileWriter("temp/linked list function.txt"))) {

            double[] xValues1 = {0.00, 0.20, 0.40, 0.60, 0.80};
            double[] yValues1 = {1.00, 1.179, 1.310, 1.390, 1.414};

            ArrayTabulatedFunction function1 = new ArrayTabulatedFunction(xValues1, yValues1);

            double[] xValues2 = {2.00, 2.10, 2.20, 2.40, 2.50};
            double[] yValues2 = {0.135, 0.122, 0.111, 0.091, 0.082};

            LinkedListTabulatedFunction function2 = new LinkedListTabulatedFunction(xValues2, yValues2);

            FunctionsIO.writeTabulatedFunction(fileWriter1, function1);
            FunctionsIO.writeTabulatedFunction(fileWriter2, function2);

            fileWriter1.close();
            fileWriter2.close();

            BufferedReader fileReader1 = new BufferedReader(new FileReader("temp/array function.txt"));
            BufferedReader fileReader2 = new BufferedReader(new FileReader("temp/linked list function.txt"));

            TabulatedFunction f1 = FunctionsIO.readTabulatedFunction(fileReader1, new ArrayTabulatedFunctionFactory());
            TabulatedFunction f2 = FunctionsIO.readTabulatedFunction(fileReader2, new LinkedListTabulatedFunctionFactory());

            assertEquals(function1.toString(), f1.toString());
            assertEquals(function2.toString(), f2.toString());

            fileReader1.close();
            fileReader2.close();
        }

        catch (IOException  e){
            e.printStackTrace();
        }
    }
    @Test
    void test3(){
        try (BufferedOutputStream output1 = new BufferedOutputStream(new FileOutputStream("temp/array function.bin"));
             BufferedOutputStream output2 = new BufferedOutputStream(new FileOutputStream("temp/linked list function.bin"))){

            double[] xValues = {1.0, 2.0, 3.0, 4.0, 5.0};
            double[] yValues = {1.0, 4.0, 9.0, 16.0, 25.0};

            TabulatedFunction arrayFunc = new ArrayTabulatedFunction(xValues, yValues);
            TabulatedFunction linkedListFunc = new LinkedListTabulatedFunction(xValues, yValues);

            FunctionsIO.writeTabulatedFunction(output1, arrayFunc);
            FunctionsIO.writeTabulatedFunction(output2, linkedListFunc);

            output1.close();
            output2.close();

            BufferedInputStream input1 = new BufferedInputStream(new FileInputStream("input/binary function.bin"));
            BufferedInputStream input2 = new BufferedInputStream(new FileInputStream("temp/linked list function.bin"));

            TabulatedFunction f1 = FunctionsIO.readTabulatedFunction(input1, new ArrayTabulatedFunctionFactory());
            TabulatedFunction f2 = FunctionsIO.readTabulatedFunction(input2, new LinkedListTabulatedFunctionFactory());

            assertEquals(arrayFunc.toString(), f1.toString());
            assertEquals(linkedListFunc.toString(), f2.toString());

            input1.close();
            input2.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        };
    }
    @Test
    void test4(){
        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter("temp/serialized array functions.xml"))) {

            double[] xValues1 = {0.00, 0.20, 0.40, 0.60, 0.80};
            double[] yValues1 = {1.00, 1.179, 1.310, 1.390, 1.414};

            ArrayTabulatedFunction function1 = new ArrayTabulatedFunction(xValues1, yValues1);

            FunctionsIO.serializeXml(fileWriter, function1);

            fileWriter.close();

            BufferedReader fileReader1 = new BufferedReader(new FileReader("temp/serialized array functions.xml"));

            ArrayTabulatedFunction f = FunctionsIO.deserializeXml(fileReader1);

            assertEquals(function1.toString(), f.toString());

            fileReader1.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}