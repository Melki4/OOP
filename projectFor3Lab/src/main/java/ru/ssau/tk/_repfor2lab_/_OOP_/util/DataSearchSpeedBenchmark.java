package ru.ssau.tk._repfor2lab_._OOP_.util;

import java.sql.*;
import java.util.concurrent.TimeUnit;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class DataSearchSpeedBenchmark {

    public static void main(String[] args) {
        System.out.println("Сравнение скорости выполнения SQL запросов...");

        Properties props = loadProperties();

        String URL = props.getProperty("spring.datasource.url");
        String USER = props.getProperty("spring.datasource.username");
        String PASSWORD = props.getProperty("spring.datasource.password");

        System.out.println("URL: " + URL);
        System.out.println("User: " + USER);

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            measureSqlSearchSpeed(connection);
        } catch (SQLException e) {
            System.err.println("Ошибка подключения к БД:");
            e.printStackTrace();
        }
    }

    private static Properties loadProperties() {
        Properties props = new Properties();

        // Прямой путь к файлу
        String filePath = "projectFor3Lab/src/main/resources/application.properties";

        try (InputStream is = new FileInputStream(filePath)) {
            props.load(is);
            System.out.println("Файл конфигурации найден: " + filePath);
        } catch (Exception e) {
            throw new RuntimeException("Не удалось найти application.properties по пути: " + filePath, e);
        }

        // Проверяем обязательные параметры
        if (!props.containsKey("spring.datasource.url") ||
                !props.containsKey("spring.datasource.username") ||
                !props.containsKey("spring.datasource.password")) {
            throw new RuntimeException("В application.properties не найдены настройки БД.\n" +
                    "Должны быть указаны: spring.datasource.url, spring.datasource.username, spring.datasource.password");
        }

        return props;
    }

    private static void measureSqlSearchSpeed(Connection connection) throws SQLException {
        System.out.println("Замер скорости SQL запросов...");

        // Тест 1: Поиск пользователя по логину
        long singleUserTime = measureSingleUserSearch(connection);

        // Тест 2: Поиск функции по ID
        long singleFunctionByIdTime = measureSingleFunctionByIdSearch(connection);

        // Тест 3: Поиск функций пользователя
        long userFunctionsTime = measureUserFunctionsSearch(connection);

        // Тест 4: Поиск точек функции
        long functionPointsTime = measureFunctionPointsSearch(connection);

        // Выводим результаты в таблицу
        System.out.println("\nРЕЗУЛЬТАТЫ СКОРОСТИ SQL ЗАПРОСОВ:");
        System.out.println("ЗАПРОС\tВРЕМЯ (микросекунды)");
        System.out.println("Поиск пользователя по логину\t" + singleUserTime);
        System.out.println("Поиск функции по ID\t" + singleFunctionByIdTime);
        System.out.println("Функции пользователя\t" + userFunctionsTime);
        System.out.println("Точки функции\t" + functionPointsTime);
    }

    private static long measureSingleUserSearch(Connection connection) throws SQLException {
        long totalTime = 0;
        int iterations = 1000;
        String sql = "SELECT * FROM users WHERE login = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "user_25");

            for (int i = 0; i < iterations; i++) {
                long startTime = System.nanoTime();
                try (ResultSet rs = stmt.executeQuery()) {
                }
                long endTime = System.nanoTime();
                totalTime += (endTime - startTime);
            }
        }

        return TimeUnit.NANOSECONDS.toMicros(totalTime / iterations);
    }

    private static long measureSingleFunctionByIdSearch(Connection connection) throws SQLException {
        long totalTime = 0;
        int iterations = 500;
        String sql = "SELECT * FROM math_functions WHERE math_function_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, 1);

            for (int i = 0; i < iterations; i++) {
                long startTime = System.nanoTime();
                try (ResultSet rs = stmt.executeQuery()) {
                }
                long endTime = System.nanoTime();
                totalTime += (endTime - startTime);
            }
        }

        return TimeUnit.NANOSECONDS.toMicros(totalTime / iterations);
    }

    private static long measureUserFunctionsSearch(Connection connection) throws SQLException {
        long totalTime = 0;
        int iterations = 200;
        String sql = "SELECT * FROM math_functions WHERE owner_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, 1);

            for (int i = 0; i < iterations; i++) {
                long startTime = System.nanoTime();
                try (ResultSet rs = stmt.executeQuery()) {
                }
                long endTime = System.nanoTime();
                totalTime += (endTime - startTime);
            }
        }

        return TimeUnit.NANOSECONDS.toMicros(totalTime / iterations);
    }

    private static long measureFunctionPointsSearch(Connection connection) throws SQLException {
        long totalTime = 0;
        int iterations = 200;
        String sql = "SELECT * FROM points WHERE function_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, 1);

            for (int i = 0; i < iterations; i++) {
                long startTime = System.nanoTime();
                try (ResultSet rs = stmt.executeQuery()) {
                }
                long endTime = System.nanoTime();
                totalTime += (endTime - startTime);
            }
        }

        return TimeUnit.NANOSECONDS.toMicros(totalTime / iterations);
    }
}