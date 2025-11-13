package ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.utils;

import java.io.IOException;
import java.util.Properties;

public class propertiesUtil {
    private static final Properties PROPERTIES = new Properties();

    static {
        loadProperties();
    }

    private static void loadProperties(){
        try(var InputStream = propertiesUtil.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            PROPERTIES.load(InputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String get(String key){
        return PROPERTIES.getProperty(key);
    }

    private propertiesUtil(){}
}
