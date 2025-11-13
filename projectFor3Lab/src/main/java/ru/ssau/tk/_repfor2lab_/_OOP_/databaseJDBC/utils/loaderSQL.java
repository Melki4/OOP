package ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.utils;

import java.io.*;

public class loaderSQL {
    public static String loadSQL(String addres){
        try (var inputStream = loaderSQL.class.getClassLoader().getResourceAsStream(addres)) {
            InputStreamReader streamReader = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(streamReader);

            String line;

            StringBuilder stringBuilder = new StringBuilder("");

            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }

            return stringBuilder.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
