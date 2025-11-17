package ru.ssau.tk._repfor2lab_._OOP_.util;

import java.sql.*;
import java.util.concurrent.TimeUnit;

public class DataSearchSpeedBenchmark {

    private static final String URL = "jdbc:postgresql://localhost:5432/math_functions_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "lera2006";

    public static void main(String[] args) {
        System.out.println("Сравнение скорости выполнения SQL запросов...");

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            measureSqlSearchSpeed(connection);
        } catch (SQLException e) {
            System.err.println("Ошибка подключения к БД:");
            e.printStackTrace();
        }
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
        String sql = "SELECT * FROM mathfunctions WHERE mathFunctionsID = ?";

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
        String sql = "SELECT * FROM mathfunctions WHERE ownerID = ?";

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
        String sql = "SELECT * FROM points WHERE functionID = ?";

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