package ru.ssau.tk._repfor2lab_._OOP_.util;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.ssau.tk._repfor2lab_._OOP_.config.AppConfig;
import ru.ssau.tk._repfor2lab_._OOP_.entities.*;
import ru.ssau.tk._repfor2lab_._OOP_.repositories.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class QuerySpeedBenchmark {

    public static void main(String[] args) {
        System.out.println("Сравнение скорости обработки запросов...");

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.register(AppConfig.class);
            context.refresh();

            UsersRepositories usersRepo = context.getBean(UsersRepositories.class);
            MathFunctionsRepositories mathFunctionsRepo = context.getBean(MathFunctionsRepositories.class);
            PointsRepositories pointsRepo = context.getBean(PointsRepositories.class);

            measureQuerySpeed(usersRepo, mathFunctionsRepo, pointsRepo);

        } catch (Exception e) {
            System.err.println("Ошибка:");
            e.printStackTrace();
        }
    }

    private static void measureQuerySpeed(UsersRepositories usersRepo,
                                          MathFunctionsRepositories mathFunctionsRepo,
                                          PointsRepositories pointsRepo) {

        System.out.println("⏱Замер скорости выполнения запросов...");

        createTestData(usersRepo, mathFunctionsRepo, pointsRepo);

        // Тест 1: Поиск пользователя по логину
        long searchUserTime = measureSearchUser(usersRepo);

        // Тест 2: Получение функций пользователя
        long getUserFunctionsTime = measureGetUserFunctions(mathFunctionsRepo);

        // Тест 3: Получение точек функции
        long getFunctionPointsTime = measureGetFunctionPoints(pointsRepo);

        // Тест 4: Создание данных (БЕЗ типа функции)
        long createDataTime = measureCreateData(usersRepo, mathFunctionsRepo, pointsRepo);

        // Выводим результаты
        System.out.println("\nРЕЗУЛЬТАТЫ:");
        System.out.println("Поиск пользователя\t" + searchUserTime);
        System.out.println("Получение функций пользователя\t" + getUserFunctionsTime);
        System.out.println("Получение точек функции\t" + getFunctionPointsTime);
        System.out.println("Создание данных\t" + createDataTime);
    }

    private static void createTestData(UsersRepositories usersRepo,
                                       MathFunctionsRepositories mathFunctionsRepo,
                                       PointsRepositories pointsRepo) {

        if (usersRepo.findByLogin("test_user").isEmpty()) {
            Users user = new Users("test_user", "password", "USER");
            usersRepo.save(user);
            System.out.println("Создан тестовый пользователь");
        }

        if (mathFunctionsRepo.count() == 0) {
            Users user = usersRepo.findByLogin("test_user").get();
            List<MathFunctions> functions = new ArrayList<>();

            for (int i = 0; i < 5; i++) {
                MathFunctions function = new MathFunctions();
                function.setNameOfFunction("test_func_" + i);
                function.setAmountOfDots(10L);
                function.setLeftBoarder(0.0);
                function.setRightBoarder(100.0);
                function.setUsers(user);
                // НЕ устанавливаем simpleFunctions
                functions.add(function);
            }
            mathFunctionsRepo.saveAll(functions);
            System.out.println("Создано функций: " + functions.size());
        }

        if (pointsRepo.count() == 0) {
            List<MathFunctions> functions = mathFunctionsRepo.findAll();
            List<Points> points = new ArrayList<>();

            for (MathFunctions function : functions) {
                for (int j = 0; j < 10; j++) {
                    Points point = new Points();
                    point.setxValue((double) j);
                    point.setyValue((double) j * 2);
                    point.setMathFunctions(function);
                    points.add(point);
                }
            }
            pointsRepo.saveAll(points);
            System.out.println("Создано точек: " + points.size());
        }
    }

    private static long measureSearchUser(UsersRepositories usersRepo) {
        long totalTime = 0;
        int iterations = 1000;

        for (int i = 0; i < iterations; i++) {
            long startTime = System.nanoTime();
            usersRepo.findByLogin("test_user");
            long endTime = System.nanoTime();
            totalTime += (endTime - startTime);
        }

        return TimeUnit.NANOSECONDS.toMicros(totalTime / iterations);
    }

    private static long measureGetUserFunctions(MathFunctionsRepositories mathFunctionsRepo) {
        List<MathFunctions> allFunctions = mathFunctionsRepo.findAll();
        if (allFunctions.isEmpty()) return 0;

        Long userId = allFunctions.get(0).getUsers().getUserID();

        long totalTime = 0;
        int iterations = 100;

        for (int i = 0; i < iterations; i++) {
            long startTime = System.nanoTime();
            mathFunctionsRepo.findByUsersUserID(userId);
            long endTime = System.nanoTime();
            totalTime += (endTime - startTime);
        }

        return TimeUnit.NANOSECONDS.toMicros(totalTime / iterations);
    }

    private static long measureGetFunctionPoints(PointsRepositories pointsRepo) {
        List<Points> allPoints = pointsRepo.findAll();
        if (allPoints.isEmpty()) return 0;

        Long functionId = allPoints.get(0).getMathFunctions().getMathFunctionsID();

        long totalTime = 0;
        int iterations = 100;

        for (int i = 0; i < iterations; i++) {
            long startTime = System.nanoTime();
            pointsRepo.findByMathFunctionsMathFunctionsID(functionId);
            long endTime = System.nanoTime();
            totalTime += (endTime - startTime);
        }

        return TimeUnit.NANOSECONDS.toMicros(totalTime / iterations);
    }

    private static long measureCreateData(UsersRepositories usersRepo,
                                          MathFunctionsRepositories mathFunctionsRepo,
                                          PointsRepositories pointsRepo) {
        long totalTime = 0;
        int iterations = 10;

        for (int i = 0; i < iterations; i++) {
            long startTime = System.nanoTime();
            Users user = new Users("temp_user_" + System.nanoTime(), "password", "USER");
            user = usersRepo.save(user);

            MathFunctions function = new MathFunctions();
            function.setNameOfFunction("temp_func_" + System.nanoTime());
            function.setAmountOfDots(5L);
            function.setLeftBoarder(0.0);
            function.setRightBoarder(10.0);
            function.setUsers(user);
            function = mathFunctionsRepo.save(function);

            List<Points> points = new ArrayList<>();
            for (int j = 0; j < 5; j++) {
                Points point = new Points();
                point.setxValue((double) j);
                point.setyValue((double) j * 2);
                point.setMathFunctions(function);
                points.add(point);
            }
            pointsRepo.saveAll(points);

            long endTime = System.nanoTime();
            totalTime += TimeUnit.NANOSECONDS.toMillis(endTime - startTime);

            pointsRepo.deleteAll(points);
            mathFunctionsRepo.delete(function);
            usersRepo.delete(user);
        }

        return totalTime / iterations;
    }
}