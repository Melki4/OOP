package ru.ssau.tk._repfor2lab_._OOP_.io;

import ru.ssau.tk._repfor2lab_._OOP_.functions.Point;
import ru.ssau.tk._repfor2lab_._OOP_.functions.TabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.factory.TabulatedFunctionFactory;

import java.io.*;
import javax.swing.text.NumberFormatter;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

final class FunctionsIO {

    private FunctionsIO(){
        throw new UnsupportedOperationException();
    }

    static void writeTabulatedFunction(BufferedWriter writer, TabulatedFunction function){

        PrintWriter boof = new PrintWriter(writer);

        boof.println(function.getCount());

        for (var point : function){
            boof.printf("%f %f\n", point.x, point.y);
        }

        boof.flush();
    }

    static void writeTabulatedFunction(BufferedOutputStream outputStream, TabulatedFunction function) throws IOException {
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        dataOutputStream.writeInt(function.getCount());
        for (Point point : function) {
            dataOutputStream.writeDouble(point.x);
            dataOutputStream.writeDouble(point.y);
        }
        dataOutputStream.flush();
    }

    static TabulatedFunction readTabulatedFunction(BufferedReader reader, TabulatedFunctionFactory factory) throws IOException{
        try {
            int amount = Integer.parseInt(reader.readLine());

            double[] xValues = new double[amount];
            double[] yValues = new double[amount];
            NumberFormat numberFormatter = NumberFormat.getInstance(Locale.forLanguageTag("ru"));

            for (int i = 0; i < amount; ++i) {
                String[] boof = reader.readLine().split(" ");
                xValues[i] = numberFormatter.parse(boof[0]).doubleValue();
                yValues[i] = numberFormatter.parse(boof[1]).doubleValue();
            }
            return factory.create(xValues, yValues);
        }
        catch (ParseException e){
            throw new IOException(e);
        }
    }
}
