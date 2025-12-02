package ru.ssau.tk._repfor2lab_._OOP_.util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class QuerySpeedBenchmark {

    private static Connection connection;

    public static void main(String[] args) {
        System.out.println("Запуск бенчмарка скорости SQL запросов...");

        try {
            // Загружаем настройки из файла
            Properties props = loadProperties();

            String url = props.getProperty("spring.datasource.url");
            String user = props.getProperty("spring.datasource.username");
            String password = props.getProperty("spring.datasource.password");

            System.out.println("Подключение к БД: " + url);

            // Подключаемся к базе
            connection = DriverManager.getConnection(url, user, password);

            // Выполняем бенчмарк
            runBenchmark();

        } catch (Exception e) {
            System.err.println("Ошибка:");
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static Properties loadProperties() {
        Properties props = new Properties();
        String filePath = "projectFor3Lab/src/main/resources/application.properties";

        try (InputStream is = new FileInputStream(filePath)) {
            props.load(is);
            System.out.println("Файл конфигурации найден: " + filePath);
        } catch (Exception e) {
            throw new RuntimeException("Не удалось найти application.properties по пути: " + filePath, e);
        }

        return props;
    }

    private static void runBenchmark() throws SQLException {
        // Создаем тестовые данные
        createTestData();

        System.out.println("\nЗАМЕР СКОРОСТИ ЗАПРОСОВ:");

        // Тест 1: Поиск пользователя по логину
        long userSearchTime = measureUserSearch();

        // Тест 2: Получение функций пользователя
        long userFunctionsTime = measureUserFunctions();

        // Тест 3: Получение точек функции
        long functionPointsTime = measureFunctionPoints();

        // Тест 4: Создание данных
        long createDataTime = measureCreateData();

        // Выводим результаты
        printResults(userSearchTime, userFunctionsTime, functionPointsTime, createDataTime);

        // Очищаем тестовые данные
        cleanupTestData();
    }

    private static void createTestData() throws SQLException {
        System.out.println("\nСоздание тестовых данных...");

        // Создаем тестового пользователя
        String userSql = "INSERT INTO users (factory_type, login, password, role) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, "benchmark");
            pstmt.setString(2, "test_user_bench");
            pstmt.setString(3, "password123");
            pstmt.setString(4, "user");
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    long userId = rs.getLong(1);

                    // Создаем 5 тестовых функций
                    List<Long> functionIds = createTestFunctions(userId, 10);

                    // Создаем точки для каждой функции
                    for (Long functionId : functionIds) {
                        createTestPoints(functionId, 1000);
                    }
                }
            }
        }

        System.out.println("Тестовые данные созданы");
    }

    private static List<Long> createTestFunctions(long userId, int count) throws SQLException {
        List<Long> functionIds = new ArrayList<>();
        String sql = "INSERT INTO math_functions (function_name, owner_id, amount_of_dots, left_boarder, right_boarder) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            for (int i = 0; i < count; i++) {
                pstmt.setString(1, "bench_func_" + i);
                pstmt.setLong(2, userId);
                pstmt.setLong(3, 10);
                pstmt.setDouble(4, 0.0);
                pstmt.setDouble(5, 100.0);
                pstmt.addBatch();
            }
            pstmt.executeBatch();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                while (rs.next()) {
                    functionIds.add(rs.getLong(1));
                }
            }
        }

        System.out.println("Создано " + count + " тестовых функций");
        return functionIds;
    }

    private static void createTestPoints(long functionId, int count) throws SQLException {
        String sql = "INSERT INTO points (function_id, x_value, y_value) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (int i = 0; i < count; i++) {
                pstmt.setLong(1, functionId);
                pstmt.setDouble(2, i * 1.0);
                pstmt.setDouble(3, i * 2.0);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }

        System.out.println("Создано " + count + " точек для функции ID=" + functionId);
    }

    private static long measureUserSearch() throws SQLException {
        System.out.println("\nТест: Поиск пользователя по логину...");

        String sql = "SELECT * FROM users WHERE login = ?";
        long totalTime = 0;
        int iterations = 1000;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "test_user_bench");

            // Разогрев
            for (int i = 0; i < 10; i++) {
                pstmt.executeQuery();
            }

            // Измерения
            for (int i = 0; i < iterations; i++) {
                long startTime = System.nanoTime();
                try (ResultSet rs = pstmt.executeQuery()) {
                    // Читаем результат
                    while (rs.next()) {
                        rs.getString("login");
                    }
                }
                long endTime = System.nanoTime();
                totalTime += (endTime - startTime);
            }
        }

        long avgTime = TimeUnit.NANOSECONDS.toMicros(totalTime / iterations);
        System.out.println("   Среднее время: " + avgTime + " мкс");
        return avgTime;
    }

    private static long measureUserFunctions() throws SQLException {
        System.out.println("\nТест: Получение функций пользователя...");

        // Сначала получаем ID тестового пользователя
        String userIdSql = "SELECT user_id FROM users WHERE login = 'test_user_bench'";
        long userId = 0;

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(userIdSql)) {
            if (rs.next()) {
                userId = rs.getLong("user_id");
            }
        }

        if (userId == 0) {
            System.out.println("   Пользователь не найден");
            return 0;
        }

        String sql = "SELECT * FROM math_functions WHERE owner_id = ?";
        long totalTime = 0;
        int iterations = 1000;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, userId);

            // Разогрев
            for (int i = 0; i < 10; i++) {
                pstmt.executeQuery();
            }

            // Измерения
            for (int i = 0; i < iterations; i++) {
                long startTime = System.nanoTime();
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        rs.getString("function_name");
                    }
                }
                long endTime = System.nanoTime();
                totalTime += (endTime - startTime);
            }
        }

        long avgTime = TimeUnit.NANOSECONDS.toMicros(totalTime / iterations);
        System.out.println("   Среднее время: " + avgTime + " мкс");
        return avgTime;
    }

    private static long measureFunctionPoints() throws SQLException {
        System.out.println("\n Тест: Получение точек функции...");

        // Получаем первую функцию пользователя
        String funcIdSql = "SELECT mf.math_function_id FROM math_functions mf " +
                "JOIN users u ON mf.owner_id = u.user_id " +
                "WHERE u.login = 'test_user_bench' LIMIT 1";

        long functionId = 0;
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(funcIdSql)) {
            if (rs.next()) {
                functionId = rs.getLong("math_function_id");
            }
        }

        if (functionId == 0) {
            System.out.println("   Функция не найдена");
            return 0;
        }

        String sql = "SELECT * FROM points WHERE function_id = ?";
        long totalTime = 0;
        int iterations = 100;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, functionId);

            // Разогрев
            for (int i = 0; i < 10; i++) {
                pstmt.executeQuery();
            }

            // Измерения
            for (int i = 0; i < iterations; i++) {
                long startTime = System.nanoTime();
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        rs.getDouble("x_value");
                        rs.getDouble("y_value");
                    }
                }
                long endTime = System.nanoTime();
                totalTime += (endTime - startTime);
            }
        }

        long avgTime = TimeUnit.NANOSECONDS.toMicros(totalTime / iterations);
        System.out.println("   Среднее время: " + avgTime + " мкс");
        return avgTime;
    }

    private static long measureCreateData() throws SQLException {
        System.out.println("\n Тест: Создание данных...");

        long totalTime = 0;
        int iterations = 1000;

        for (int iter = 0; iter < iterations; iter++) {
            long startTime = System.nanoTime();

            // Создание пользователя
            String userSql = "INSERT INTO users (factory_type, login, password, role) VALUES (?, ?, ?, ?)";
            long userId = 0;

            try (PreparedStatement pstmt = connection.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, "temp");
                pstmt.setString(2, "temp_user_" + System.nanoTime());
                pstmt.setString(3, "temp_pass");
                pstmt.setString(4, "user");
                pstmt.executeUpdate();

                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        userId = rs.getLong(1);
                    }
                }
            }

            // Создание функции
            String funcSql = "INSERT INTO math_functions (function_name, owner_id, amount_of_dots, left_boarder, right_boarder) VALUES (?, ?, ?, ?, ?)";
            long functionId = 0;

            try (PreparedStatement pstmt = connection.prepareStatement(funcSql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, "temp_func_" + System.nanoTime());
                pstmt.setLong(2, userId);
                pstmt.setLong(3, 5);
                pstmt.setDouble(4, 0.0);
                pstmt.setDouble(5, 10.0);
                pstmt.executeUpdate();

                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        functionId = rs.getLong(1);
                    }
                }
            }

            // Создание точек
            String pointsSql = "INSERT INTO points (function_id, x_value, y_value) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(pointsSql)) {
                for (int i = 0; i < 5; i++) {
                    pstmt.setLong(1, functionId);
                    pstmt.setDouble(2, i * 1.0);
                    pstmt.setDouble(3, i * 2.0);
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }

            long endTime = System.nanoTime();
            totalTime += TimeUnit.NANOSECONDS.toMicros(endTime - startTime);

            // Удаление временных данных
            deleteTempData(userId, functionId);
        }

        long avgTime = totalTime / iterations;
        System.out.println("   Среднее время: " + avgTime + " мкс");
        return avgTime;
    }

    private static void deleteTempData(long userId, long functionId) throws SQLException {
        // Удаляем точки
        String deletePointsSql = "DELETE FROM points WHERE function_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deletePointsSql)) {
            pstmt.setLong(1, functionId);
            pstmt.executeUpdate();
        }

        // Удаляем функцию
        String deleteFuncSql = "DELETE FROM math_functions WHERE math_function_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteFuncSql)) {
            pstmt.setLong(1, functionId);
            pstmt.executeUpdate();
        }

        // Удаляем пользователя
        String deleteUserSql = "DELETE FROM users WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteUserSql)) {
            pstmt.setLong(1, userId);
            pstmt.executeUpdate();
        }
    }

    private static void cleanupTestData() throws SQLException {
        System.out.println("\nОчистка тестовых данных...");

        // Находим тестового пользователя
        String findUserSql = "SELECT user_id FROM users WHERE login = 'test_user_bench'";
        long userId = 0;

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(findUserSql)) {
            if (rs.next()) {
                userId = rs.getLong("user_id");
            }
        }

        if (userId == 0) {
            System.out.println("Тестовые данные не найдены");
            return;
        }

        // Удаляем точки тестовых функций
        String deletePointsSql = "DELETE FROM points WHERE function_id IN (SELECT math_function_id FROM math_functions WHERE owner_id = ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(deletePointsSql)) {
            pstmt.setLong(1, userId);
            pstmt.executeUpdate();
        }

        // Удаляем тестовые функции
        String deleteFuncsSql = "DELETE FROM math_functions WHERE owner_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteFuncsSql)) {
            pstmt.setLong(1, userId);
            pstmt.executeUpdate();
        }

        // Удаляем тестового пользователя
        String deleteUserSql = "DELETE FROM users WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteUserSql)) {
            pstmt.setLong(1, userId);
            pstmt.executeUpdate();
        }

        System.out.println("Тестовые данные очищены");
    }

    private static void printResults(long userSearchTime, long userFunctionsTime,
                                     long functionPointsTime, long createDataTime) {
        System.out.println("\n" + "═".repeat(60));
        System.out.println(" РЕЗУЛЬТАТЫ БЕНЧМАРКА");
        System.out.println("═".repeat(60));
        System.out.printf("%-35s %-15s %-15s%n", "ОПЕРАЦИЯ", "ВРЕМЯ (мкс)", "ВРЕМЯ (мс)");
        System.out.println("-".repeat(60));

        printRow("Поиск пользователя по логину", userSearchTime);
        printRow("Получение функций пользователя", userFunctionsTime);
        printRow("Получение точек функции", functionPointsTime);
        printRow("Создание данных (с очисткой)", createDataTime);

        System.out.println("═".repeat(60));
    }

    private static void printRow(String operation, long timeMicros) {
        double timeMs = timeMicros / 1000.0;
        System.out.printf("%-35s %-15d %-15.3f%n", operation, timeMicros, timeMs);
    }
}