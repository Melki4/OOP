package ru.ssau.tk._repfor2lab_._OOP_.model.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class connectionManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(connectionManager.class);

    static {
        try {
            // ЯВНАЯ загрузка драйвера
            Class.forName("org.postgresql.Driver");
            LOGGER.info("PostgreSQL Driver loaded successfully");
        } catch (ClassNotFoundException e) {
            LOGGER.error("PostgreSQL Driver not found!", e);
            throw new RuntimeException(e);
        }
    }

    public static Connection open(){
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "postgres";
        String password = "4sfl8gpassword";

        try {
            LOGGER.info("Подключаемся к: {}", url);
            LOGGER.info("Логин: {}", user);

            Connection connection = DriverManager.getConnection(url, user, password);
            LOGGER.info("Связь с БД установлена успешно");
            return connection;

        } catch (SQLException e) {
            LOGGER.error("Ошибка при подключении к бд: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}