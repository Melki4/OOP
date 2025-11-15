package ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class mathFunctionsInterfaceTest {
    private mathFunctionsInterface mathFunctionsInterface;

    @BeforeEach
    void setUp() {
        mathFunctionsInterface = new mathFunctionsInterface();
        mathFunctionsInterface.createTable();
    }

    @AfterEach
    void tearDown() {
        // Очищаем тестовые данные
        List<String> allFunctions = mathFunctionsInterface.selectAllMathFunctions();
        for (String function : allFunctions) {
            String[] parts = function.split(" ");
            int functionCode = Integer.parseInt(parts[0]);
            // Удаляем тестовые функции
            mathFunctionsInterface.deleteMathFunctionByFunctionId(functionCode);
        }
    }

    @Test
    void testFullCrudForMathFunctions() {
        // CREATE - Добавляем несколько функций
        mathFunctionsInterface.createTable();

        //INSERT
        simpleFunctionInterface s = new simpleFunctionInterface();
        s.addSimpleFunction("SqrFunc", "Квадратичная функция");

        userInterface u = new userInterface();
        u.addUser("array", "login", "hardpassword", "user");
        int id = u.selectIdByLogin("login");

        mathFunctionsInterface.addMathFunction("x^2-1", 100, -42.2,
                42.2, id, "SqrFunc");
        mathFunctionsInterface.addMathFunction("x^3-1", 100, -42.2,
                42.2, id, "SqrFunc");
        mathFunctionsInterface.addMathFunction("x^4-1", 100, -42.2,
                42.2, id, "SqrFunc");

        // READ - Получаем все функции
        List<String> allFunctions = mathFunctionsInterface.selectAllMathFunctions();
        assertFalse(allFunctions.isEmpty());

        List<String> sinLocalName = mathFunctionsInterface.selectMathFunctionByUserId(id);
        assertNotNull(sinLocalName);

        // UPDATE - Обновляем локальное имя
        mathFunctionsInterface.updateFunctionNameByFunctionId("dsdsd", 2);

        // DELETE - Удаляем одну функцию
        mathFunctionsInterface.deleteMathFunctionByFunctionId(2);

        s.deleteAllFunctions();
    }
}