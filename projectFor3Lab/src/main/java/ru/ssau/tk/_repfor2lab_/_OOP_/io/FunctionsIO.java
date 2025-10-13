package ru.ssau.tk._repfor2lab_._OOP_.io;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.AnyTypePermission;
import ru.ssau.tk._repfor2lab_._OOP_.functions.ArrayTabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.Point;
import ru.ssau.tk._repfor2lab_._OOP_.functions.TabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.factory.TabulatedFunctionFactory;

import java.io.*;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

final class FunctionsIO {

    private FunctionsIO(){
        throw new UnsupportedOperationException();
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

    static void writeTabulatedFunction(BufferedWriter writer, TabulatedFunction function){

        PrintWriter boof = new PrintWriter(writer);

        boof.println(function.getCount());

        for (var point : function){
            boof.printf("%f %f\n", point.x, point.y);
        }

        boof.flush();
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

    static TabulatedFunction readTabulatedFunction(BufferedInputStream inputStream, TabulatedFunctionFactory factory) throws IOException {
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        int count = dataInputStream.readInt();

        double[] xValues = new double[count];
        double[] yValues = new double[count];

        for (int i = 0; i < count; i++){
            xValues[i] = dataInputStream.readDouble();
            yValues[i] = dataInputStream.readDouble();
        }
        return factory.create(xValues, yValues);
    }

    static void serialize(BufferedOutputStream stream, TabulatedFunction function) throws IOException {
        ObjectOutputStream boof = new ObjectOutputStream(stream);
        boof.writeObject(function);
        boof.flush();
    }

    static TabulatedFunction deserialize(BufferedInputStream stream) throws IOException, ClassNotFoundException {
        ObjectInputStream stream1 = new ObjectInputStream(stream);
        return (TabulatedFunction) stream1.readObject();
    }

    static void serializeXml(BufferedWriter writer, ArrayTabulatedFunction function) throws IOException {
        XStream stream = new XStream();
        stream.allowTypesByWildcard(new String[]{
                "ru.ssau.tk.**",
                "java.util.**",
                "[D",
                "[Ljava.lang.Double;"
        });
        writer.write(stream.toXML(function));
        writer.flush();
    }

    static ArrayTabulatedFunction deserializeXml(BufferedReader reader) throws IOException {
        XStream stream = new XStream();
        stream.allowTypesByWildcard(new String[]{
                "ru.ssau.tk.**",
                "java.util.**",
                "[D",
                "[Ljava.lang.Double;"
        });
        return (ArrayTabulatedFunction) stream.fromXML(reader);
    }
    static void serializeJson(BufferedWriter writer, ArrayTabulatedFunction function) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        writer.write(objectMapper.writeValueAsString(function));
        writer.flush();
    }

    static ArrayTabulatedFunction deserializeJson(BufferedReader reader) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readerFor(ArrayTabulatedFunction.class).readValue(reader);
    }
}
