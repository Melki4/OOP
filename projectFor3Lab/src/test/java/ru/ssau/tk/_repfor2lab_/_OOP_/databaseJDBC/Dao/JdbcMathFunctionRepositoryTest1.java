package ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.Dao;

import org.junit.jupiter.api.*;
import ru.ssau.tk._repfor2lab_._OOP_.databaseEnteties.MathFunctions;
import ru.ssau.tk._repfor2lab_._OOP_.exceptions.DataDoesNotExistException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class JdbcMathFunctionRepositoryTest1 {

    static JdbcUserRepository userRepository;
    static JdbcSimpleFunctionRepository simpleFunctionRepository;
    static JdbcMathFunctionRepository mathFunctionRepository;

    static int userIdVova;
    static int userIdYojo;
    static String quadraticFunctionType = "Квадратичная функция";
    static String tabulatedFunctionType = "Табулированная функция";

    @BeforeAll
    static void setUp() {
        userRepository = new JdbcUserRepository();
        simpleFunctionRepository = new JdbcSimpleFunctionRepository();
        mathFunctionRepository = new JdbcMathFunctionRepository();

        // Создаем таблицы
        userRepository.createTable();
        simpleFunctionRepository.createTable();
        mathFunctionRepository.createTable();

        // Создаем пользователей
        userRepository.createUser("array", "vovapain", "vivatVOVA", "user");
        userRepository.createUser("list", "yojo", "vivatyojo", "admin");

        userIdVova = userRepository.selectIdByLogin("vovapain");
        userIdYojo = userRepository.selectIdByLogin("yojo");

        // Создаем типы функций
        simpleFunctionRepository.createSimpleFunction(quadraticFunctionType);
        simpleFunctionRepository.createSimpleFunction(tabulatedFunctionType);

        // Создаем математические функции
        mathFunctionRepository.createMathFunction("x^2", 100, -1, 13, userIdYojo, quadraticFunctionType);
        mathFunctionRepository.createMathFunction("x^2", 1000, -1, 13, userIdYojo, quadraticFunctionType);
        mathFunctionRepository.createMathFunction("e^(x^2-1)", 100, -1, 123, userIdVova, tabulatedFunctionType);
        mathFunctionRepository.createMathFunction("sin(x)", 500, -Math.PI, Math.PI, userIdVova, tabulatedFunctionType);
    }

    @AfterAll
    static void tearDown() {
        mathFunctionRepository.deleteAllFunctions();
        simpleFunctionRepository.deleteAllFunctions();
        userRepository.deleteAllUsers();
    }

    @Order(1)
    @Test
    void findMathFunctionsByUserId_ShouldReturnUserFunctions() {
        List<MathFunctions> vovaFunctions = mathFunctionRepository.findMathFunctionsByUserId(userIdVova);
        List<MathFunctions> yojoFunctions = mathFunctionRepository.findMathFunctionsByUserId(userIdYojo);

        assertNotNull(vovaFunctions, "Функции пользователя vovapain не должны быть null");
        assertNotNull(yojoFunctions, "Функции пользователя yojo не должны быть null");

        assertEquals(2, vovaFunctions.size(), "У vovapain должно быть 2 функции");
        assertEquals(2, yojoFunctions.size(), "У yojo должно быть 2 функции");

        // Проверяем, что все функции принадлежат правильным пользователям
        for (MathFunctions func : vovaFunctions) {
            assertEquals(userIdVova, func.getOwnerId(), "Функция должна принадлежать vovapain");
        }

        for (MathFunctions func : yojoFunctions) {
            assertEquals(userIdYojo, func.getOwnerId(), "Функция должна принадлежать yojo");
        }

        // Проверяем заполненность полей
        for (MathFunctions func : vovaFunctions) {
            assertTrue(func.getFunctionId() > 0, "ID функции должен быть положительным");
            assertNotNull(func.getFunctionName(), "Имя функции не должно быть null");
            assertTrue(func.getAmountOfDots() > 0, "Количество точек должно быть положительным");
            assertNotNull(func.getFunctionType(), "Тип функции не должен быть null");
        }
    }

    @Order(2)
    @Test
    void findMathFunctionsByUserId_WithNonExistentUser_ShouldThrowException() {
        int nonExistentUserId = 999999;

        assertThrows(DataDoesNotExistException.class,
                () -> mathFunctionRepository.findMathFunctionsByUserId(nonExistentUserId),
                "Должно бросаться исключение для несуществующего пользователя");
    }

    @Order(3)
    @Test
    void findMathFunctionsByName_ShouldReturnFunctionsWithGivenName() {
        List<MathFunctions> xSquaredFunctions = mathFunctionRepository.findMathFunctionsByName("x^2");
        List<MathFunctions> sinFunctions = mathFunctionRepository.findMathFunctionsByName("sin(x)");

        assertEquals(2, xSquaredFunctions.size(), "Должно быть 2 функции с именем x^2");
        assertEquals(1, sinFunctions.size(), "Должна быть 1 функция с именем sin(x)");

        // Проверяем, что все возвращенные функции имеют правильное имя
        for (MathFunctions func : xSquaredFunctions) {
            assertEquals("x^2", func.getFunctionName(), "Имя функции должно быть x^2");
        }

        for (MathFunctions func : sinFunctions) {
            assertEquals("sin(x)", func.getFunctionName(), "Имя функции должно быть sin(x)");
        }
    }

    @Order(4)
    @Test
    void findMathFunctionsByName_WithNonExistentName_ShouldReturnEmptyList() {
        List<MathFunctions> nonExistentFunctions = mathFunctionRepository.findMathFunctionsByName("nonexistent_function");

        assertNotNull(nonExistentFunctions, "Результат не должен быть null");
        assertTrue(nonExistentFunctions.isEmpty(), "Список должен быть пустым для несуществующего имени");
    }

    @Order(5)
    @Test
    void findMathFunctionComplex_ShouldReturnExactFunction() {
        MathFunctions function = mathFunctionRepository.findMathFunctionComplex(-1, 13, 100, "x^2");

        assertNotNull(function, "Функция не должна быть null");
        assertEquals("x^2", function.getFunctionName(), "Имя функции должно совпадать");
        assertEquals(-1, function.getLeftBorder(), 0.001, "Левая граница должна совпадать");
        assertEquals(13, function.getRightBorder(), 0.001, "Правая граница должна совпадать");
        assertEquals(100, function.getAmountOfDots(), "Количество точек должно совпадать");
        assertEquals(quadraticFunctionType, function.getFunctionType(), "Тип функции должен совпадать");
        assertEquals(userIdYojo, function.getOwnerId(), "ID владельца должен совпадать");
    }

    @Order(6)
    @Test
    void findMathFunctionComplex_WithNonExistentParameters_ShouldThrowException() {
        assertThrows(DataDoesNotExistException.class,
                () -> mathFunctionRepository.findMathFunctionComplex(0, 1, 100, "Несуществующий тип"),
                "Должно бросаться исключение для несуществующих параметров");
    }

    @Order(7)
    @Test
    void findMathFunctionIdComplex_ShouldReturnCorrectId() {
        Integer functionId = mathFunctionRepository.findMathFunctionIdComplex(-1, 13, 100, "x^2");

        assertNotNull(functionId, "ID функции не должен быть null");
        assertTrue(functionId > 0, "ID функции должен быть положительным");

        // Проверяем, что найденный ID соответствует реальной функции
        MathFunctions function = mathFunctionRepository.findMathFunctionComplex(-1, 13, 100, "x^2");
        assertEquals(functionId, function.getFunctionId(), "ID должен совпадать с ID найденной функции");
    }

    @Order(8)
    @Test
    void updateFunctionNameByFunctionId_ShouldUpdateSuccessfully() {
        MathFunctions originalFunction = mathFunctionRepository.findMathFunctionComplex(-1, 13, 100,"x^2");
        String newName = "que";

        mathFunctionRepository.updateFunctionNameByFunctionId(newName, originalFunction.getFunctionId());

        // Проверяем обновление
        MathFunctions updatedFunction = mathFunctionRepository.findMathFunctionComplex(-1, 13, 100, "que");
        assertEquals(newName, updatedFunction.getFunctionName(), "Имя функции должно быть обновлено");

        // Проверяем, что другие поля не изменились
        assertEquals(originalFunction.getLeftBorder(), updatedFunction.getLeftBorder(), 0.001);
        assertEquals(originalFunction.getRightBorder(), updatedFunction.getRightBorder(), 0.001);
        assertEquals(originalFunction.getAmountOfDots(), updatedFunction.getAmountOfDots());
        assertEquals(originalFunction.getFunctionType(), updatedFunction.getFunctionType());
        assertEquals(originalFunction.getOwnerId(), updatedFunction.getOwnerId());
    }

    @Order(11)
    @Test
    void deleteMathFunctionsByUserId_ShouldRemoveAllUserFunctions() {
        int initialVovaCount = mathFunctionRepository.findMathFunctionsByUserId(userIdVova).size();

        mathFunctionRepository.deleteMathFunctionsByUserId(userIdYojo);

        // Проверяем, что функции пользователя yojo удалены
        assertThrows(DataDoesNotExistException.class,
                () -> mathFunctionRepository.findMathFunctionsByUserId(userIdYojo),
                "Должно бросаться исключение при поиске функций удаленного пользователя");

        // Проверяем, что функции пользователя vovapain не затронуты
        List<MathFunctions> vovaFunctions = mathFunctionRepository.findMathFunctionsByUserId(userIdVova);
        assertEquals(initialVovaCount, vovaFunctions.size(),
                "Функции пользователя vovapain не должны быть затронуты");
    }

    @Order(13)
    @Test
    void integrationTest_ComplexFunctionLifecycle() {
        // Комплексный сценарий: создание → поиск → обновление → удаление
        String newFunctionName = "cos(x)";
        double leftBorder = 0;
        double rightBorder = 2 * 3.1415926535;
        int amountOfDots = 1000;

        // Создаем новую функцию
        mathFunctionRepository.createMathFunction(newFunctionName, amountOfDots, leftBorder,
                rightBorder, userIdVova, tabulatedFunctionType);

        // Проверяем создание
        assertTrue(mathFunctionRepository.existsFunctionComplex(leftBorder, rightBorder,
                amountOfDots, "cos(x)"));

        // Находим функцию
        MathFunctions createdFunction = mathFunctionRepository.findMathFunctionComplex(
                leftBorder, rightBorder, amountOfDots, "cos(x)");
        assertEquals(newFunctionName, createdFunction.getFunctionName());

        // Обновляем имя
        String updatedName = "cosine_function";
        mathFunctionRepository.updateFunctionNameByFunctionId(updatedName, createdFunction.getFunctionId());

        // Проверяем обновление
        MathFunctions updatedFunction = mathFunctionRepository.findMathFunctionComplex(
                leftBorder, rightBorder, amountOfDots, "cosine_function");
        assertEquals(updatedName, updatedFunction.getFunctionName());

        // Удаляем функцию
        mathFunctionRepository.deleteMathFunctionByFunctionId(updatedFunction.getFunctionId());

        // Проверяем удаление
        assertFalse(mathFunctionRepository.existsFunctionComplex(leftBorder, rightBorder,
                amountOfDots, "cosine_function"));
    }

    @Order(14)
    @Test
    void createMathFunction_WithDuplicateParameters_ShouldHandleAppropriately() {
        // Попытка создать функцию с такими же параметрами как существующая
        assertDoesNotThrow(() ->
                mathFunctionRepository.createMathFunction("x^2", 100, -1, 13, userIdYojo, quadraticFunctionType));

        // Проверяем, что исходные данные не повреждены
        List<MathFunctions> functions = mathFunctionRepository.findMathFunctionsByName("x^2");
        assertFalse(functions.isEmpty(), "Функции с именем x^2 должны существовать");
    }

    @Order(15)
    @Test
    void deleteAllFunctions_ShouldRemoveAllData() {
        mathFunctionRepository.deleteAllFunctions();

        // Проверяем, что все функции удалены
        assertThrows(DataDoesNotExistException.class,
                () -> mathFunctionRepository.findMathFunctionsByUserId(userIdVova),
                "Должно бросаться исключение после удаления всех функций");

        assertThrows(DataDoesNotExistException.class,
                () -> mathFunctionRepository.findMathFunctionsByUserId(userIdYojo),
                "Должно бросаться исключение после удаления всех функций");
    }
}