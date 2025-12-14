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
        String host = getEnvOrDefault("DB_HOST", "localhost");
        String port = getEnvOrDefault("DB_PORT", "5432");
        String database = getEnvOrDefault("DB_NAME", "postgres");
        String user = getEnvOrDefault("DB_USER", "postgres");
        String password = getEnvOrDefault("DB_PASSWORD", "4sfl8gpassword");
        String url = String.format("jdbc:postgresql://%s:%s/%s", host, port, database);

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

    private static String getEnvOrDefault(String key, String defaultValue) {
        String value = System.getenv(key);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        return value;
    }
}