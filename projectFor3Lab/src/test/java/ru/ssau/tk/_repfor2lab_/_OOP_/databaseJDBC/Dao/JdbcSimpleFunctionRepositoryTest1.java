package ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.Dao;

import org.junit.jupiter.api.*;
import ru.ssau.tk._repfor2lab_._OOP_.databaseEnteties.SimpleFunctions;
import ru.ssau.tk._repfor2lab_._OOP_.exceptions.DataDoesNotExistException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class JdbcSimpleFunctionRepositoryTest1 {

    static List<String> array = new ArrayList<>();
    static JdbcSimpleFunctionRepository repository;

    @BeforeAll
    static void setUp() {
        repository = new JdbcSimpleFunctionRepository();
        repository.createTable();

        array.add("Квадратичная функция");
        array.add("Постоянная единичная");
        array.add("Сложная");
        array.add("Табулированная");
        array.add("A_Функция с особым именем");
        array.add("Z_Последняя функция");

        // Добавляем функции с проверкой на дубликаты
        for (String functionName : array) {
            if (!repository.existSimpleFunction(functionName)) {
                repository.createSimpleFunction(functionName);
            }
        }
    }

    @AfterAll
    static void tearDown() {
        repository.deleteAllFunctions();
    }

    @BeforeEach
    void resetToInitialState() {
        // Восстанавливаем исходное состояние перед каждым тестом
        repository.deleteAllFunctions();
        for (String functionName : array) {
            repository.createSimpleFunction(functionName);
        }
    }

    @Order(1)
    @Test
    void findAllSimpleFunctions_ShouldReturnAllFunctions() {
        List<SimpleFunctions> result = repository.findAllSimpleFunctions();

        assertNotNull(result, "Результат не должен быть null");
        assertEquals(array.size(), result.size(), "Количество функций должно совпадать");

        List<String> resultNames = result.stream()
                .map(SimpleFunctions::getLocalName)
                .collect(Collectors.toList());

        assertTrue(resultNames.containsAll(array), "Все исходные функции должны присутствовать в результате");
    }

    @Order(2)
    @Test
    void findAllSimpleFunctionsSortedByLocalName_ShouldReturnSortedList() {
        List<String> expectedSorted = new ArrayList<>(array);
        expectedSorted.sort(Comparator.naturalOrder());

        List<SimpleFunctions> result = repository.findAllSimpleFunctionsSortedByLocalName();

        assertEquals(expectedSorted.size(), result.size(), "Количество элементов должно совпадать");

        for (int i = 0; i < expectedSorted.size(); i++) {
            assertEquals(expectedSorted.get(i), result.get(i).getLocalName(),
                    "Функции должны быть отсортированы в лексикографическом порядке");
        }

        // Проверяем, что список действительно отсортирован
        List<String> resultNames = result.stream()
                .map(SimpleFunctions::getLocalName)
                .collect(Collectors.toList());
        List<String> manuallySorted = resultNames.stream()
                .sorted()
                .collect(Collectors.toList());
        assertEquals(manuallySorted, resultNames, "Список должен быть отсортирован");
    }

    @Order(3)
    @Test
    void updateSimpleFunctionName_ShouldUpdateExistingFunction() {
        String oldName = "Постоянная единичная";
        String newName = "Постоянная восьмеричная";

        assertTrue(repository.existSimpleFunction(oldName), "Исходная функция должна существовать");

        repository.updateSimpleFunctionName(oldName, newName);

        assertFalse(repository.existSimpleFunction(oldName), "Старое имя не должно существовать");
        assertTrue(repository.existSimpleFunction(newName), "Новое имя должно существовать");

        // Проверяем, что другие функции не затронуты
        List<SimpleFunctions> allFunctions = repository.findAllSimpleFunctions();
        assertEquals(array.size(), allFunctions.size(), "Общее количество функций не должно измениться");
    }

    @Order(4)
    @Test
    void updateSimpleFunctionName_WithNonExistentFunction_ShouldNotAffectOtherFunctions() {
        String nonExistentName = "Несуществующая функция";
        String newName = "Новое имя";

        assertFalse(repository.existSimpleFunction(nonExistentName));

        // Должен выполниться без ошибок, но не изменить данные
        assertDoesNotThrow(() -> repository.updateSimpleFunctionName(nonExistentName, newName));

        List<SimpleFunctions> allFunctions = repository.findAllSimpleFunctions();
        assertEquals(array.size(), allFunctions.size(), "Количество функций не должно измениться");
    }

    @Order(5)
    @Test
    void deleteSimpleFunctionByName_ShouldRemoveFunction() {
        String functionToDelete = "Табулированная";

        assertTrue(repository.existSimpleFunction(functionToDelete));

        repository.deleteSimpleFunctionByName(functionToDelete);

        assertFalse(repository.existSimpleFunction(functionToDelete));

        List<SimpleFunctions> remainingFunctions = repository.findAllSimpleFunctions();
        assertEquals(array.size() - 1, remainingFunctions.size(), "Количество функций должно уменьшиться на 1");

        // Проверяем, что удалена именно нужная функция
        List<String> remainingNames = remainingFunctions.stream()
                .map(SimpleFunctions::getLocalName)
                .collect(Collectors.toList());
        assertFalse(remainingNames.contains(functionToDelete), "Удаленная функция не должна присутствовать в результате");
    }

    @Order(6)
    @Test
    void deleteSimpleFunctionByName_WithNonExistentFunction_ShouldNotAffectData() {
        String nonExistentFunction = "Несуществующая функция для удаления";

        assertFalse(repository.existSimpleFunction(nonExistentFunction));

        assertDoesNotThrow(() -> repository.deleteSimpleFunctionByName(nonExistentFunction));

        List<SimpleFunctions> allFunctions = repository.findAllSimpleFunctions();
        assertEquals(array.size(), allFunctions.size(), "Количество функций не должно измениться");
    }

    @Order(7)
    @Test
    void existSimpleFunction_ShouldReturnCorrectBoolean() {
        assertTrue(repository.existSimpleFunction("Квадратичная функция"),
                "Существующая функция должна возвращать true");

        assertFalse(repository.existSimpleFunction("Несуществующая функция"),
                "Несуществующая функция должна возвращать false");

        // Проверяем граничные случаи
        assertFalse(repository.existSimpleFunction(""), "Пустая строка должна возвращать false");
        assertFalse(repository.existSimpleFunction(null), "Null должен возвращать false");
    }

    @Order(8)
    @Test
    void createSimpleFunction_WithDuplicateName_ShouldHandleGracefully() {
        String duplicateName = "Квадратичная функция";

        assertTrue(repository.existSimpleFunction(duplicateName));

        // Попытка создать дубликат (поведение зависит от реализации - может бросить исключение или проигнорировать)
        assertThrows(RuntimeException.class, () -> repository.createSimpleFunction(duplicateName));

        // Проверяем, что функция все еще существует
        assertTrue(repository.existSimpleFunction(duplicateName));
    }

    @Order(9)
    @Test
    void deleteAllFunctions_ShouldRemoveAllData() {
        List<SimpleFunctions> beforeDeletion = repository.findAllSimpleFunctions();
        assertFalse(beforeDeletion.isEmpty(), "Перед удалением должны быть функции");

        repository.deleteAllFunctions();

        // После удаления таблица должна быть пустой
        assertThrows(DataDoesNotExistException.class, () -> repository.findAllSimpleFunctions(),
                "После удаления всех функций должен бросаться DataDoesNotExistException");
    }

    @Order(10)
    @Test
    void integrationTest_ComplexScenario() {
        // Комплексный сценарий: создание → обновление → удаление → проверка
        String newFunction = "Новая тестовая функция";

        // Создаем новую функцию
        repository.createSimpleFunction(newFunction);
        assertTrue(repository.existSimpleFunction(newFunction));

        // Обновляем её имя
        String updatedName = "Обновленная тестовая функция";
        repository.updateSimpleFunctionName(newFunction, updatedName);
        assertTrue(repository.existSimpleFunction(updatedName));
        assertFalse(repository.existSimpleFunction(newFunction));

        // Удаляем её
        repository.deleteSimpleFunctionByName(updatedName);
        assertFalse(repository.existSimpleFunction(updatedName));

        // Проверяем, что исходные данные не затронуты
        List<SimpleFunctions> remainingFunctions = repository.findAllSimpleFunctions();
        assertEquals(array.size(), remainingFunctions.size(),
                "Исходные данные не должны быть затронуты комплексным сценарием");
    }

    @Order(11)
    @Test
    void findAllSimpleFunctions_WithEmptyTable_ShouldThrowException() {
        repository.deleteAllFunctions();

        assertThrows(DataDoesNotExistException.class, () -> repository.findAllSimpleFunctions(),
                "Пустая таблица должна бросать DataDoesNotExistException");

        assertThrows(DataDoesNotExistException.class, () -> repository.findAllSimpleFunctionsSortedByLocalName(),
                "Пустая таблица должна бросать DataDoesNotExistException при сортированном запросе");
    }
}