package ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.ssau.tk._repfor2lab_._OOP_.databaseDTO.MathFunctions;
import ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.Dao.JdbcMathFunctionRepository;
import ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.Dao.JdbcPointRepository;
import ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.Dao.JdbcSimpleFunctionRepository;
import ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.Dao.JdbcUserRepository;
import ru.ssau.tk._repfor2lab_._OOP_.exceptions.DataDoesNotExistException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class jdbcMathFunctionsRepositoryDTOTest {
    private JdbcMathFunctionRepository mathFunctionRepository;
    private JdbcSimpleFunctionRepository simpleFunctionRepository;
    private JdbcUserRepository userRepository;

    @BeforeEach
    void setUp() {
        mathFunctionRepository = new JdbcMathFunctionRepository();
        simpleFunctionRepository = new JdbcSimpleFunctionRepository();
        userRepository = new JdbcUserRepository();

        // Создаем таблицы
        mathFunctionRepository.createTable();
        simpleFunctionRepository.createTable();
        userRepository.createTable();
    }

    @AfterEach
    void tearDown() {
        simpleFunctionRepository.deleteAllFunctions();
        var p = new JdbcPointRepository();
        p.deleteAllPoints();
        mathFunctionRepository.deleteAllFunctions();
        simpleFunctionRepository.deleteAllFunctions();
        userRepository.deleteAllUsers();
    }

    @Test
    void testMathFunctionCRUDOperations() {
        // Подготовка данных - все add методы void
        simpleFunctionRepository.addSimpleFunction("SqrFunc", "Квадратичная функция");
        userRepository.addUser("array", "login", "hardpassword", "user");
        userRepository.addUser("array", "login1", "hardpassword", "user");

        // Получаем ID пользователей
        int userId1 = userRepository.selectIdByLogin("login");
        int userId2 = userRepository.selectIdByLogin("login1");

        // CREATE - Добавляем математические функции (void методы)
        mathFunctionRepository.addMathFunction("x^2-1", 100, -42.2, 42.2, userId1, "SqrFunc");
        mathFunctionRepository.addMathFunction("x^3-1", 100, -42.2, 42.2, userId2, "SqrFunc");
        mathFunctionRepository.addMathFunction("x^4-1", 100, -42.2, 42.2, userId2, "SqrFunc");

        // READ - Проверяем что функции добавились
        List<MathFunctions> allFunctions = mathFunctionRepository.selectAllMathFunctions();
        assertFalse(allFunctions.isEmpty());
        assertEquals(3, allFunctions.size());

        // Проверяем функции конкретного пользователя
        List<MathFunctions> user2Functions = mathFunctionRepository.selectMathFunctionsByUserId(userId2);
        assertEquals(2, user2Functions.size());

        List<MathFunctions> user1Functions = mathFunctionRepository.selectMathFunctionsByUserId(userId1);
        assertEquals(1, user1Functions.size());

        // Проверяем получение функции по имени
        MathFunctions functionByName = mathFunctionRepository.selectMathFunctionsByName("x^2-1");
        assertNotNull(functionByName);
        assertEquals("x^2-1", functionByName.getFunctionName());
        assertEquals(userId1, functionByName.getOwnerId().intValue());
        assertEquals(100L, functionByName.getAmountOfDots().longValue());
        assertEquals(-42.2, functionByName.getLeftBorder(), 0.001);
        assertEquals(42.2, functionByName.getRightBorder(), 0.001);

        // Проверяем сортировку по логинам
        List<MathFunctions> sortedFunctions = mathFunctionRepository.selectAllMathFunctionsSortedByUserLogins();
        assertFalse(sortedFunctions.isEmpty());
        assertEquals(3, sortedFunctions.size());

        // Проверяем существование функции
        assertTrue(mathFunctionRepository.existsFunction("x^3-1"));
        assertFalse(mathFunctionRepository.existsFunction("non-existent"));

        // UPDATE - Обновляем имя функции
        MathFunctions functionToUpdate = mathFunctionRepository.selectMathFunctionsByName("x^2-1");
        mathFunctionRepository.updateFunctionNameByFunctionId("x^2-updated", functionToUpdate.getFunctionId().intValue());

        // Проверяем обновление
        MathFunctions updatedFunction = mathFunctionRepository.selectMathFunctionsByName("x^2-updated");
        assertNotNull(updatedFunction);
        assertEquals("x^2-updated", updatedFunction.getFunctionName());

        // Проверяем что старая функция больше не существует
        assertFalse(mathFunctionRepository.existsFunction("x^2-1"));

        // DELETE - Удаляем функцию
        MathFunctions functionToDelete = mathFunctionRepository.selectMathFunctionsByName("x^4-1");
        mathFunctionRepository.deleteMathFunctionByFunctionId(functionToDelete.getFunctionId().intValue());

        // Проверяем удаление
        List<MathFunctions> functionsAfterDelete = mathFunctionRepository.selectAllMathFunctions();
        assertEquals(2, functionsAfterDelete.size());
        assertFalse(mathFunctionRepository.existsFunction("x^4-1"));
    }

    @Test
    void testEmptyDatabaseBehavior() {
        // Тестируем поведение с пустой базой данных

        assertThrows(DataDoesNotExistException.class, ()->{mathFunctionRepository.selectAllMathFunctions();});

        // Проверяем что исключения выбрасываются для несуществующих данных
        assertThrows(RuntimeException.class, () -> {
            mathFunctionRepository.selectMathFunctionsByName("non-existent");
        });

        assertThrows(RuntimeException.class, () -> {
            mathFunctionRepository.selectMathFunctionsByUserId(999);
        });
    }

    @Test
    void testFunctionRelationshipsAndDataIntegrity() {
        // Тестируем связи между сущностями
        simpleFunctionRepository.addSimpleFunction("Linear", "Линейная функция");
        simpleFunctionRepository.addSimpleFunction("Quadratic", "Квадратичная функция");

        userRepository.addUser("array", "testuser", "password", "user");
        userRepository.addUser("list", "adminuser", "adminpass", "admin");

        int testUserId = userRepository.selectIdByLogin("testuser");
        int adminUserId = userRepository.selectIdByLogin("adminuser");

        // Добавляем функции для разных пользователей
        mathFunctionRepository.addMathFunction("2*x+1", 50, -10.0, 10.0, testUserId, "Linear");
        mathFunctionRepository.addMathFunction("x^2", 100, -5.0, 5.0, adminUserId, "Quadratic");
        mathFunctionRepository.addMathFunction("3*x", 75, -15.0, 15.0, testUserId, "Linear");

        // Проверяем связи и целостность данных
        List<MathFunctions> allFunctions = mathFunctionRepository.selectAllMathFunctions();
        assertEquals(3, allFunctions.size());

        // Проверяем что у каждого MathFunction есть правильные связи
        for (MathFunctions function : allFunctions) {
            assertNotNull(function.getFunctionId());
            assertNotNull(function.getFunctionName());
            assertNotNull(function.getAmountOfDots());
            assertNotNull(function.getLeftBorder());
            assertNotNull(function.getRightBorder());
            assertNotNull(function.getOwnerId());
            assertNotNull(function.getOwnerLogin());
            assertNotNull(function.getFunctionType());

            // Проверяем что логин пользователя корректен
            if (function.getOwnerId().intValue() == testUserId) {
                assertEquals("testuser", function.getOwnerLogin());
            } else if (function.getOwnerId().intValue() == adminUserId) {
                assertEquals("adminuser", function.getOwnerLogin());
            }

            // Проверяем что тип функции корректен
            assertTrue(function.getFunctionType().equals("Linear") ||
                    function.getFunctionType().equals("Quadratic"));
        }

        // Проверяем распределение функций по пользователям
        List<MathFunctions> testUserFunctions = mathFunctionRepository.selectMathFunctionsByUserId(testUserId);
        List<MathFunctions> adminUserFunctions = mathFunctionRepository.selectMathFunctionsByUserId(adminUserId);

        assertEquals(2, testUserFunctions.size());
        assertEquals(1, adminUserFunctions.size());

        // Проверяем что все функции testuser имеют правильный логин
        for (MathFunctions function : testUserFunctions) {
            assertEquals("testuser", function.getOwnerLogin());
        }

        // Проверяем что все функции adminuser имеют правильный логин
        for (MathFunctions function : adminUserFunctions) {
            assertEquals("adminuser", function.getOwnerLogin());
        }
    }

    @Test
    void testTableCreationAndCleanup() {
        // Тестируем что таблица создается правильно
        mathFunctionRepository.createTable();

        // После создания таблицы должны успешно работать операции
        simpleFunctionRepository.addSimpleFunction("TestFunc", "Тестовая функция");
        userRepository.addUser("array", "test", "pass", "user");

        int userId = userRepository.selectIdByLogin("test");
        mathFunctionRepository.addMathFunction("test", 10, 0.0, 1.0, userId, "TestFunc");

        List<MathFunctions> functions = mathFunctionRepository.selectAllMathFunctions();
        assertEquals(1, functions.size());

        // Тестируем очистку
        mathFunctionRepository.deleteAllFunctions();
        assertThrows(DataDoesNotExistException.class, ()->{mathFunctionRepository.selectAllMathFunctions();});
    }
}