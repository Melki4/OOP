package ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.Dao;

import org.junit.jupiter.api.*;
import ru.ssau.tk._repfor2lab_._OOP_.databaseDTO.Points;
import ru.ssau.tk._repfor2lab_._OOP_.exceptions.DataDoesNotExistException;
import ru.ssau.tk._repfor2lab_._OOP_.functions.ArrayTabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.MathFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.Point;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class JdbcPointRepositoryTest1 {

    static JdbcUserRepository userRepository;
    static JdbcSimpleFunctionRepository simpleFunctionRepository;
    static JdbcMathFunctionRepository mathFunctionRepository;
    static JdbcPointRepository pointRepository;

    static int functionId;
    static int emptyFunctionId;
    static List<Point> initialPoints;
    static int userIdYojo;

    @BeforeAll
    static void setUp() {
        userRepository = new JdbcUserRepository();
        simpleFunctionRepository = new JdbcSimpleFunctionRepository();
        mathFunctionRepository = new JdbcMathFunctionRepository();
        pointRepository = new JdbcPointRepository();

        // Создаем таблицы
        userRepository.createTable();
        simpleFunctionRepository.createTable();
        mathFunctionRepository.createTable();
        pointRepository.createTable();

        // Создаем пользователей
        userRepository.createUser("array", "vovapain", "vivatVOVA", "user");
        userRepository.createUser("list", "yojo", "vivatyojo", "admin");

        userIdYojo = userRepository.selectIdByLogin("yojo");

        // Создаем типы функций
        simpleFunctionRepository.createSimpleFunction("Квадратичная функция");
        simpleFunctionRepository.createSimpleFunction("Табулированная функция");

        // Создаем математические функции
        mathFunctionRepository.createMathFunction("x^2", 100, -1, 13, userIdYojo, "Квадратичная функция");
        mathFunctionRepository.createMathFunction("Test Function", 10, -100.0, 100.0, userIdYojo, "Табулированная функция");

        functionId = mathFunctionRepository.findMathFunctionIdComplex(-100.0, 100.0, 10, "Test Function");

        // Создаем пустую функцию для тестов
        mathFunctionRepository.createMathFunction("Empty Function", 0, 0, 1, userIdYojo, "Табулированная функция");
        emptyFunctionId = mathFunctionRepository.findMathFunctionIdComplex(0, 1, 0, "Empty Function");

        // Генерируем точки для тестовой функции
        MathFunction mathFunction = (double x) -> x * x + 2 * x + 1;
        ArrayTabulatedFunction arrayFunction = new ArrayTabulatedFunction(mathFunction, -100.0, 100.0, 10);

        initialPoints = new ArrayList<>();
        for (int i = 0; i < arrayFunction.getCount(); i++) {
            Point p = new Point(arrayFunction.getX(i), arrayFunction.getY(i));
            initialPoints.add(p);
        }

        // Добавляем точки в базу
        pointRepository.addManyPoints(initialPoints, functionId);
    }

    @AfterAll
    static void tearDown() {
        pointRepository.deleteAllPoints();
        mathFunctionRepository.deleteAllFunctions();
        simpleFunctionRepository.deleteAllFunctions();
        userRepository.deleteAllUsers();
    }

    @BeforeEach
    void resetToInitialState() {
        // Восстанавливаем исходное состояние перед каждым тестом
        pointRepository.deletePointsByFunctionId(functionId);
        pointRepository.addManyPoints(initialPoints, functionId);
    }

    @Order(1)
    @Test
    void findPointsByFunctionId_ShouldReturnAllPoints() {
        List<Points> points = pointRepository.findPointsByFunctionId(functionId);

        assertNotNull(points, "Список точек не должен быть null");
        assertEquals(initialPoints.size(), points.size(), "Количество точек должно совпадать");

        // Проверяем, что все точки принадлежат правильной функции
        for (Points point : points) {
            assertEquals(functionId, point.getFunctionId(), "Точка должна принадлежать правильной функции");
            assertNotNull(point.getXValue(), "Координата X не должна быть null");
            assertNotNull(point.getYValue(), "Координата Y не должна быть null");
        }

        // Проверяем соответствие координат
        List<Double> actualXValues = points.stream()
                .map(Points::getXValue)
                .sorted()
                .collect(Collectors.toList());
        List<Double> expectedXValues = initialPoints.stream()
                .map(p -> p.x)
                .sorted()
                .collect(Collectors.toList());

        assertArrayEquals(expectedXValues.toArray(), actualXValues.toArray(),
                "Координаты X должны совпадать с исходными");
    }

    @Order(2)
    @Test
    void findPointsByFunctionId_WithEmptyFunction_ShouldThrowException() {
        assertThrows(DataDoesNotExistException.class,
                () -> pointRepository.findPointsByFunctionId(emptyFunctionId),
                "Должно бросаться исключение для функции без точек");
    }

    @Order(3)
    @Test
    void findPointsByFunctionId_WithNonExistentFunction_ShouldThrowException() {
        int nonExistentFunctionId = 999999;

        assertThrows(DataDoesNotExistException.class,
                () -> pointRepository.findPointsByFunctionId(nonExistentFunctionId),
                "Должно бросаться исключение для несуществующей функции");
    }

    @Order(4)
    @Test
    void findPointsByFunctionIdSorted_ShouldReturnSortedPoints() {
        List<Points> sortedPoints = pointRepository.findPointsByFunctionIdSorted(functionId);

        assertNotNull(sortedPoints, "Отсортированный список не должен быть null");
        assertEquals(initialPoints.size(), sortedPoints.size(), "Количество точек должно совпадать");

        // Проверяем, что точки отсортированы по X
        for (int i = 0; i < sortedPoints.size() - 1; i++) {
            assertTrue(sortedPoints.get(i).getXValue() <= sortedPoints.get(i + 1).getXValue(),
                    "Точки должны быть отсортированы по возрастанию X");
        }

        // Проверяем, что все исходные точки присутствуют
        List<Double> sortedXValues = sortedPoints.stream()
                .map(Points::getXValue)
                .collect(Collectors.toList());
        List<Double> expectedXValues = initialPoints.stream()
                .map(p -> p.x)
                .sorted()
                .collect(Collectors.toList());

        assertArrayEquals(expectedXValues.toArray(), sortedXValues.toArray(),
                "Отсортированные координаты X должны совпадать с ожидаемыми");
    }

    @Order(6)
    @Test
    void updateXValueByFunctionIdAndOldX_WithNonExistentPoint_ShouldNotAffectData() {
        double nonExistentX = 999999.0;
        double newX = 100000.0;

        int pointsCountBefore = pointRepository.findPointsByFunctionId(functionId).size();

        assertDoesNotThrow(() ->
                pointRepository.updateXValueByFunctionIdAndOldX(nonExistentX, functionId, newX));

        int pointsCountAfter = pointRepository.findPointsByFunctionId(functionId).size();
        assertEquals(pointsCountBefore, pointsCountAfter,
                "Количество точек не должно измениться при обновлении несуществующей точки");
    }

    @Order(8)
    @Test
    void createPoint_ShouldAddNewPoint() {
        double newX = 99999.0;
        double newY = 99999.0;

        int pointsCountBefore = pointRepository.findPointsByFunctionId(functionId).size();

        pointRepository.createPoint(newX, newY, functionId);

        int pointsCountAfter = pointRepository.findPointsByFunctionId(functionId).size();
        assertEquals(pointsCountBefore + 1, pointsCountAfter,
                "Количество точек должно увеличиться на 1");

        // Проверяем, что новая точка существует
        List<Points> points = pointRepository.findPointsByFunctionId(functionId);
        boolean newPointExists = points.stream()
                .anyMatch(p -> Math.abs(p.getXValue() - newX) < 0.001 && Math.abs(p.getYValue() - newY) < 0.001);

        assertTrue(newPointExists, "Новая точка должна существовать в базе");
    }

    @Order(10)
    @Test
    void addManyPoints_ShouldAddAllPoints() {
        // Создаем новый набор точек
        MathFunction newFunction = (double x) -> Math.sin(x);
        ArrayTabulatedFunction newArrayFunction = new ArrayTabulatedFunction(newFunction, 0, Math.PI, 5);

        List<Point> newPoints = new ArrayList<>();
        for (int i = 0; i < newArrayFunction.getCount(); i++) {
            newPoints.add(new Point(newArrayFunction.getX(i), newArrayFunction.getY(i)));
        }

        // Добавляем точки
        pointRepository.addManyPoints(newPoints, emptyFunctionId);

        // Проверяем результат
        List<Points> pointsAfter = pointRepository.findPointsByFunctionId(emptyFunctionId);
        assertEquals(newPoints.size(), pointsAfter.size(),
                "Все новые точки должны быть добавлены");

        // Проверяем координаты
        List<Double> actualXValues = pointsAfter.stream()
                .map(Points::getXValue)
                .sorted()
                .collect(Collectors.toList());
        List<Double> expectedXValues = newPoints.stream()
                .map(p -> p.x)
                .sorted()
                .collect(Collectors.toList());

        assertArrayEquals(expectedXValues.toArray(), actualXValues.toArray(),
                "Координаты X добавленных точек должны совпадать");
    }

    @Order(11)
    @Test
    void addManyPoints_WithEmptyList_ShouldNotAffectData() {
        List<Point> emptyPoints = new ArrayList<>();

        int pointsCountBefore = pointRepository.findPointsByFunctionId(functionId).size();

        pointRepository.addManyPoints(emptyPoints, functionId);

        int pointsCountAfter = pointRepository.findPointsByFunctionId(functionId).size();
        assertEquals(pointsCountBefore, pointsCountAfter,
                "Количество точек не должно измениться при добавлении пустого списка");
    }

    @Order(12)
    @Test
    void deletePointsByFunctionId_ShouldRemoveAllFunctionPoints() {
        // Создаем дополнительные точки в другой функции
        mathFunctionRepository.createMathFunction("Temp Function", 5, 0, 10, userIdYojo, "Табулированная функция");
        int tempFunctionId = mathFunctionRepository.findMathFunctionIdComplex(0, 10, 5, "Temp Function");

        List<Point> tempPoints = List.of(
                new Point(1.0, 1.0),
                new Point(2.0, 4.0),
                new Point(3.0, 9.0)
        );
        pointRepository.addManyPoints(tempPoints, tempFunctionId);

        // Удаляем точки основной функции
        pointRepository.deletePointsByFunctionId(functionId);

        // Проверяем, что точки основной функции удалены
        assertThrows(DataDoesNotExistException.class,
                () -> pointRepository.findPointsByFunctionId(functionId),
                "Должно бросаться исключение после удаления всех точек функции");

        // Проверяем, что точки временной функции не затронуты
        List<Points> tempFunctionPoints = pointRepository.findPointsByFunctionId(tempFunctionId);
        assertEquals(tempPoints.size(), tempFunctionPoints.size(),
                "Точки временной функции не должны быть затронуты");

        // Очищаем временные данные
        pointRepository.deletePointsByFunctionId(tempFunctionId);
        mathFunctionRepository.deleteMathFunctionByFunctionId(tempFunctionId);
    }

    @Order(13)
    @Test
    void deletePointsByFunctionId_WithNonExistentFunction_ShouldNotAffectData() {
        int nonExistentFunctionId = 999999;

        int pointsCountBefore = pointRepository.findPointsByFunctionId(functionId).size();

        assertDoesNotThrow(() -> pointRepository.deletePointsByFunctionId(nonExistentFunctionId));

        int pointsCountAfter = pointRepository.findPointsByFunctionId(functionId).size();
        assertEquals(pointsCountBefore, pointsCountAfter,
                "Количество точек не должно измениться при удалении точек несуществующей функции");
    }

    @Order(15)
    @Test
    void deleteAllPoints_ShouldRemoveAllPointsFromAllFunctions() {
        pointRepository.deleteAllPoints();

        // Проверяем, что все точки удалены
        assertThrows(DataDoesNotExistException.class,
                () -> pointRepository.findPointsByFunctionId(functionId),
                "Должно бросаться исключение после удаления всех точек");

        assertThrows(DataDoesNotExistException.class,
                () -> pointRepository.findPointsByFunctionId(emptyFunctionId),
                "Должно бросаться исключение для всех функций после удаления всех точек");
    }
}