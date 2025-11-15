package ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class loaderSQL {

    private static final Logger LOGGER = LoggerFactory.getLogger(loaderSQL.class);

    public static String loadSQL(String addres){
        LOGGER.info("Пробуем спарсить sql запрос");
        try (var inputStream = loaderSQL.class.getClassLoader().getResourceAsStream(addres)) {
            InputStreamReader streamReader = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(streamReader);

            String line;

            StringBuilder stringBuilder = new StringBuilder("");

            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            LOGGER.info("Запрос успешно спаршен");
            return stringBuilder.toString();
        } catch (IOException e) {
            LOGGER.warn("Произошла ошибка при парсинге запроса");
            throw new RuntimeException(e);
        }
    }
}
