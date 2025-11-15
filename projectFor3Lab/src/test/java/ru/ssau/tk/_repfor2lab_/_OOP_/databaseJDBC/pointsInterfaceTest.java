package ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC;

import org.junit.jupiter.api.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class pointsInterfaceTest {

    private pointsInterface pointsInterface;
    private simpleFunctionInterface simpleFunctionInterface;

    @BeforeEach
    void setUp() {
        pointsInterface = new pointsInterface();
        simpleFunctionInterface = new simpleFunctionInterface();

        // таблица функций нужна, т.к. в points есть внешний ключ
        simpleFunctionInterface.createTable();
        pointsInterface.createTable();
    }

    @AfterEach
    void tearDown() {
        pointsInterface.deleteAllFunctions();
        simpleFunctionInterface.deleteAllFunctions();
    }

    @Test
    @DisplayName("Полный CRUD для points")
    void testFullCrudForPoints() {
        simpleFunctionInterface.deleteAllFunctions();
        mathFunctionsInterface s = new mathFunctionsInterface();
        userInterface u = new userInterface();
        u.addUser("array", "login", "hardpassword", "user");
        int id = u.selectIdByLogin("login");
        s.addMathFunction("x^2-1", 100, -42.2,
                42.2, id, "SqrFunc");
        int i = Integer.parseInt(s.selectAllMathFunctions().get(0).split(" ")[0]);

        // Добавляем базовую функцию, чтобы id подходил
        simpleFunctionInterface.addSimpleFunction("TEST_FUNC", "Тестовая функция");

        // Выбираем id функции
        String local = simpleFunctionInterface.selectSimpleFunctionByFunctionCode("TEST_FUNC");
        assertEquals("Тестовая функция", local);

        // Добавляем точки
        pointsInterface.addPoint(1.1, 2.2, i);
        pointsInterface.addPoint(3.3, 4.4, i);
        pointsInterface.addPoint(5.5, 6.6, i);

        List<String> allPoints = pointsInterface.selectAllPoints();
        assertFalse(allPoints.isEmpty());
        assertTrue(allPoints.size() >= 3);

        // Проверяем выборку по id функции
        List<String> functionPoints = pointsInterface.selectPointsById(i);
        assertEquals(3, functionPoints.size());

        // Разбираем строку и проверяем корректность
        String pointString = functionPoints.get(0);
        String[] parts = pointString.split(" ");

        assertEquals(5, parts.length);
        double x = Double.parseDouble(parts[1]);
        double y = Double.parseDouble(parts[2]);

        assertEquals(1.1, x);
        assertEquals(2.2, y);

        // UPDATE X/Y
        pointsInterface.updateXValueById(10.0, Integer.parseInt(parts[0]));
        pointsInterface.updateYValueById(20.0, Integer.parseInt(parts[0]));

        List<String> updated = pointsInterface.selectPointsById(i);
        String updatedPointStr = updated.get(2).split(" ")[0];
        int updatedId = Integer.parseInt(updatedPointStr);

        String[] updParts = updated.get(2).split(" ");
        assertEquals(10.0, Double.parseDouble(updParts[1]));
        assertEquals(20.0, Double.parseDouble(updParts[2]));

        // DELETE
        pointsInterface.deletePointById(updatedId);

        List<String> afterDelete = pointsInterface.selectPointsById(i);
        assertEquals(2, afterDelete.size());
    }

    @Test
    @DisplayName("Добавление, выборка и массовое удаление точек")
    void testMassInsertAndDelete() {

        mathFunctionsInterface s = new mathFunctionsInterface();
        userInterface u = new userInterface();

        int user_id = u.selectIdByLogin("login");

        s.addMathFunction("x^2-1", 100, -42.2,
                42.2, user_id, "SqrFunc");

        int function_id = Integer.parseInt(s.selectAllMathFunctions().get(0).split(" ")[0]);

        simpleFunctionInterface.addSimpleFunction("F", "Функция");

        // Массовое добавление
        for (int i = 0; i < 10; i++) {
            pointsInterface.addPoint((double) i, (double) (i * 2), function_id);
        }

        List<String> all = pointsInterface.selectAllPoints();
        assertEquals(10, all.size());

        // deleteAllFunctions
        pointsInterface.deleteAllFunctions();

        List<String> empty = pointsInterface.selectAllPoints();
        assertTrue(empty.isEmpty());
    }

    @Test
    @DisplayName("Изменение X и Y по ID точки")
    void testUpdateOperations() {

        mathFunctionsInterface ss = new mathFunctionsInterface();
        userInterface u = new userInterface();

        int user_id = u.selectIdByLogin("login");

        ss.addMathFunction("x^2-1", 100, -42.2,
                42.2, user_id, "SqrFunc");

        int function_id = Integer.parseInt(ss.selectAllMathFunctions().get(0).split(" ")[0]);

        simpleFunctionInterface.addSimpleFunction("F2", "Функция 2");

        pointsInterface.addPoint(7.7, 8.8, function_id);

        List<String> before = pointsInterface.selectAllPoints();
        assertEquals(1, before.size());

        int id = Integer.parseInt(before.get(0).split(" ")[0]);

        pointsInterface.updateXValueById(100.0, id);
        pointsInterface.updateYValueById(200.0, id);

        List<String> after = pointsInterface.selectAllPoints().stream()
                .filter(s -> Integer.parseInt(s.split(" ")[0]) == id)
                .toList();

        assertEquals(1, after.size());

        String[] parts = after.get(0).split(" ");
        assertEquals(100.0, Double.parseDouble(parts[1]));
        assertEquals(200.0, Double.parseDouble(parts[2]));
    }
}
