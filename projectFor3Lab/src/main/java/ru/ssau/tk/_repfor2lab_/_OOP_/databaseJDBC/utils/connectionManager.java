package ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class connectionManager {

    private final static String URL_KEY = "db.url";
    private final static String LOGIN_KEY = "db.login";
    private final static String PASSWORD_KEY = "db.password";
    private static final Logger LOGGER = LoggerFactory.getLogger(connectionManager.class);

    public static Connection open(){
        try {
            LOGGER.info("Начинаем устанавливать связь с бд");
            var To_return = DriverManager.getConnection(propertiesUtil.get(URL_KEY),
                    propertiesUtil.get(LOGIN_KEY), propertiesUtil.get(PASSWORD_KEY));
            LOGGER.info("Связь установлена");
            return To_return;
        } catch (SQLException e) {
            LOGGER.warn("Произошла ошибка при подключении к бд");
            throw new RuntimeException(e);
        }
    }
}
