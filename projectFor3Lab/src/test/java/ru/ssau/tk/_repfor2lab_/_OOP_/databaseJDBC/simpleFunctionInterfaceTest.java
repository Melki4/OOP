package ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC;

import org.junit.jupiter.api.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class simpleFunctionInterfaceTest {

    private simpleFunctionInterface simpleFunctionInterface;

    @BeforeEach
    void setUp() {
        var s = new simpleFunctionInterface();
        s.deleteAllFunctions();
        simpleFunctionInterface = new simpleFunctionInterface();
        simpleFunctionInterface.createTable();
    }

    @AfterEach
    void tearDown() {
        // Очищаем тестовые данные
        var s = new simpleFunctionInterface();
        s.deleteAllFunctions();
    }

    @Test
    void testFullCrudForSimpleFunctions() {
        // CREATE - Добавляем несколько функций
        simpleFunctionInterface.addSimpleFunction("SIN", "Синус");
        simpleFunctionInterface.addSimpleFunction("COS", "Косинус");
        simpleFunctionInterface.addSimpleFunction("TAN", "Тангенс");

        // READ - Получаем все функции
        List<String> allFunctions = simpleFunctionInterface.selectAllSimpleFunctions();
        assertFalse(allFunctions.isEmpty());

        // Ищем конкретную функцию по коду
        String sinLocalName = simpleFunctionInterface.selectSimpleFunctionByFunctionCode("SIN");
        assertNotNull(sinLocalName);
        assertEquals("Синус", sinLocalName);

        String cosLocalName = simpleFunctionInterface.selectSimpleFunctionByFunctionCode("COS");
        assertNotNull(cosLocalName);
        assertEquals("Косинус", cosLocalName);

        // UPDATE - Обновляем локальное имя
        simpleFunctionInterface.updateLocalNameByFunctionCode("Синус функция", "SIN");

        // Проверяем обновление
        String updatedSinLocalName = simpleFunctionInterface.selectSimpleFunctionByFunctionCode("SIN");
        assertEquals("Синус функция", updatedSinLocalName);

        // DELETE - Удаляем одну функцию
        simpleFunctionInterface.deleteSimpleFunctionByFunctionCode("TAN");

        // Проверяем что функция удалена
        assertThrows(RuntimeException.class, () -> {
            simpleFunctionInterface.selectSimpleFunctionByFunctionCode("TAN");
        });
    }

    @Test
    @DisplayName("Тест с различными кодами функций и локальными именами")
    void testVariousFunctionCodesAndLocalNames() {
        // Добавляем функции с разными кодами и именами
        simpleFunctionInterface.addSimpleFunction("TEST_LOG", "Логарифм");
        simpleFunctionInterface.addSimpleFunction("TEST_EXP", "Экспонента");
        simpleFunctionInterface.addSimpleFunction("TEST_SQRT", "Квадратный корень");
        simpleFunctionInterface.addSimpleFunction("TEST_POW", "Степень");

        // Проверяем добавление
        List<String> allFunctions = simpleFunctionInterface.selectAllSimpleFunctions();
        assertTrue(allFunctions.size() >= 4);

        // Проверяем поиск по каждому коду
        assertEquals("Логарифм", simpleFunctionInterface.selectSimpleFunctionByFunctionCode("TEST_LOG"));
        assertEquals("Экспонента", simpleFunctionInterface.selectSimpleFunctionByFunctionCode("TEST_EXP"));
        assertEquals("Квадратный корень", simpleFunctionInterface.selectSimpleFunctionByFunctionCode("TEST_SQRT"));
        assertEquals("Степень", simpleFunctionInterface.selectSimpleFunctionByFunctionCode("TEST_POW"));

        // Обновляем все локальные имена
        simpleFunctionInterface.updateLocalNameByFunctionCode("Натуральный логарифм", "TEST_LOG");
        simpleFunctionInterface.updateLocalNameByFunctionCode("Экспоненциальная функция", "TEST_EXP");
        simpleFunctionInterface.updateLocalNameByFunctionCode("Корень квадратный", "TEST_SQRT");

        // Проверяем обновления
        assertEquals("Натуральный логарифм", simpleFunctionInterface.selectSimpleFunctionByFunctionCode("TEST_LOG"));
        assertEquals("Экспоненциальная функция", simpleFunctionInterface.selectSimpleFunctionByFunctionCode("TEST_EXP"));
        assertEquals("Корень квадратный", simpleFunctionInterface.selectSimpleFunctionByFunctionCode("TEST_SQRT"));

        // Удаляем несколько функций
        simpleFunctionInterface.deleteSimpleFunctionByFunctionCode("TEST_LOG");
        simpleFunctionInterface.deleteSimpleFunctionByFunctionCode("TEST_EXP");

        // Проверяем что удаленные функции больше не находятся
        assertThrows(RuntimeException.class, () ->
                simpleFunctionInterface.selectSimpleFunctionByFunctionCode("TEST_LOG")
        );
        assertThrows(RuntimeException.class, () ->
                simpleFunctionInterface.selectSimpleFunctionByFunctionCode("TEST_EXP")
        );

        // Проверяем что оставшиеся функции все еще существуют
        assertEquals("Корень квадратный", simpleFunctionInterface.selectSimpleFunctionByFunctionCode("TEST_SQRT"));
        assertEquals("Степень", simpleFunctionInterface.selectSimpleFunctionByFunctionCode("TEST_POW"));
    }
}