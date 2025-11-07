package ru.ssau.tk._repfor2lab_._OOP_.io;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.xstream.XStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ssau.tk._repfor2lab_._OOP_.functions.ArrayTabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.Point;
import ru.ssau.tk._repfor2lab_._OOP_.functions.TabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.factory.TabulatedFunctionFactory;

import java.io.*;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

final class FunctionsIO {

    private static final Logger LOGGER = LoggerFactory.getLogger(FunctionsIO.class);

    private FunctionsIO(){
        LOGGER.warn("Ошибка в сериализации");
        throw new UnsupportedOperationException();
    }

    static void writeTabulatedFunction(BufferedOutputStream outputStream, TabulatedFunction function) throws IOException {
        LOGGER.trace("Начинаем записывать в битовый поток{}", outputStream.toString());
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        dataOutputStream.writeInt(function.getCount());
        for (Point point : function) {
            dataOutputStream.writeDouble(point.x);
            dataOutputStream.writeDouble(point.y);
        }
        dataOutputStream.flush();
        LOGGER.trace("закончили запись в битовый поток");
    }

    static void writeTabulatedFunction(BufferedWriter writer, TabulatedFunction function){
        LOGGER.trace("Начинаем записывать в поток{}", writer.toString());
        PrintWriter boof = new PrintWriter(writer);

        boof.println(function.getCount());

        for (var point : function){
            boof.printf("%f %f\n", point.x, point.y);
        }

        boof.flush();
        LOGGER.trace("закончили запись в поток");
    }

    static TabulatedFunction readTabulatedFunction(BufferedReader reader, TabulatedFunctionFactory factory) throws IOException{
        try {
            LOGGER.trace("Начинаем попытку сериализации");
            int amount = Integer.parseInt(reader.readLine());

            double[] xValues = new double[amount];
            double[] yValues = new double[amount];
            NumberFormat numberFormatter = NumberFormat.getInstance(Locale.forLanguageTag("ru"));

            for (int i = 0; i < amount; ++i) {
                String[] boof = reader.readLine().split(" ");
                xValues[i] = numberFormatter.parse(boof[0]).doubleValue();
                yValues[i] = numberFormatter.parse(boof[1]).doubleValue();
            }
            LOGGER.trace("всё прошло хорошо, создаём функцию");
            return factory.create(xValues, yValues);
        }
        catch (ParseException e){
            throw new IOException(e);
        }
    }

    static TabulatedFunction readTabulatedFunction(BufferedInputStream inputStream, TabulatedFunctionFactory factory) throws IOException {
        LOGGER.trace("Начинаем попытку сериализации из битового файла");
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        int count = dataInputStream.readInt();

        double[] xValues = new double[count];
        double[] yValues = new double[count];

        for (int i = 0; i < count; i++){
            xValues[i] = dataInputStream.readDouble();
            yValues[i] = dataInputStream.readDouble();
        }
        LOGGER.trace("хорошо прошло по сериализации из битового файла, пробуем создать табулированную ф-цию");
        return factory.create(xValues, yValues);
    }

    static void serialize(BufferedOutputStream stream, TabulatedFunction function) throws IOException {
        LOGGER.trace("Попытка сериализовать");
        ObjectOutputStream boof = new ObjectOutputStream(stream);
        boof.writeObject(function);
        boof.flush();
        LOGGER.trace("всё прошло успешно");
    }

    static TabulatedFunction deserialize(BufferedInputStream stream) throws IOException, ClassNotFoundException {
        LOGGER.trace("Попытка десериализовать");
        ObjectInputStream stream1 = new ObjectInputStream(stream);
        LOGGER.trace("всё прошло успешно, приводим к табулированной функции при возвращении");
        return (TabulatedFunction) stream1.readObject();
    }

    static void serializeXml(BufferedWriter writer, ArrayTabulatedFunction function) throws IOException {
        LOGGER.trace("Попытка сериализовать в xml");
        XStream stream = new XStream();
        stream.allowTypesByWildcard(new String[]{"ru.ssau.tk.**"});
        writer.write(stream.toXML(function));
        writer.flush();
        LOGGER.trace("всё прошло успешно в xml");
    }

    static ArrayTabulatedFunction deserializeXml(BufferedReader reader) throws IOException {
        LOGGER.trace("Попытка десериализовать xml");
        XStream stream = new XStream();
        stream.allowTypesByWildcard(new String[]{"ru.ssau.tk.**"});
        LOGGER.trace("всё прошло успешно в xml возвращаем и приводим к массиву");
        return (ArrayTabulatedFunction) stream.fromXML(reader);
    }

    static void serializeJson(BufferedWriter writer, ArrayTabulatedFunction function) throws IOException {
        LOGGER.trace("Попытка сериализовать в json");
        ObjectMapper objectMapper = new ObjectMapper();
        writer.write(objectMapper.writeValueAsString(function));
        writer.flush();
        LOGGER.trace("всё прошло успешно в json");
    }

    static ArrayTabulatedFunction deserializeJson(BufferedReader reader) throws IOException {
        LOGGER.trace("Попытка десериализовать json");
        ObjectMapper objectMapper = new ObjectMapper();
        LOGGER.trace("всё прошло успешно в json возвращаем и приводим к массиву");
        return objectMapper.readerFor(ArrayTabulatedFunction.class).readValue(reader);
    }
}
