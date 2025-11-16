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
        assertEquals("Синус", sinLocalName.split(" ")[2]);

        String cosLocalName = simpleFunctionInterface.selectSimpleFunctionByFunctionCode("COS");
        assertNotNull(cosLocalName);
        assertEquals("Косинус", cosLocalName.split(" ")[2]);

        // UPDATE - Обновляем локальное имя
        simpleFunctionInterface.updateLocalNameByFunctionCode("Синус_функция", "SIN");

        // Проверяем обновление
        String updatedSinLocalName = simpleFunctionInterface.selectSimpleFunctionByFunctionCode("SIN");
        assertEquals("Синус_функция", updatedSinLocalName.split(" ")[2]);

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
        simpleFunctionInterface.addSimpleFunction("TEST_SQRT", "Квадратный_корень");
        simpleFunctionInterface.addSimpleFunction("TEST_POW", "Степень");

        // Проверяем добавление
        List<String> allFunctions = simpleFunctionInterface.selectAllSimpleFunctions();
        assertTrue(allFunctions.size() >= 4);

        // Проверяем поиск по каждому коду
        assertEquals("Логарифм", simpleFunctionInterface.selectSimpleFunctionByFunctionCode("TEST_LOG").split(" ")[2]);
        assertEquals("Экспонента", simpleFunctionInterface.selectSimpleFunctionByFunctionCode("TEST_EXP").split(" ")[2]);
        assertEquals("Квадратный_корень", simpleFunctionInterface.selectSimpleFunctionByFunctionCode("TEST_SQRT").split(" ")[2]);
        assertEquals("Степень", simpleFunctionInterface.selectSimpleFunctionByFunctionCode("TEST_POW").split(" ")[2]);

        // Обновляем все локальные имена
        simpleFunctionInterface.updateLocalNameByFunctionCode("Натуральный_логарифм", "TEST_LOG");
        simpleFunctionInterface.updateLocalNameByFunctionCode("Экспоненциальная_функция", "TEST_EXP");
        simpleFunctionInterface.updateLocalNameByFunctionCode("Корень_квадратный", "TEST_SQRT");

        // Проверяем обновления
        assertEquals("Натуральный_логарифм", simpleFunctionInterface.selectSimpleFunctionByFunctionCode("TEST_LOG").split(" ")[2]);
        assertEquals("Экспоненциальная_функция", simpleFunctionInterface.selectSimpleFunctionByFunctionCode("TEST_EXP").split(" ")[2]);
        assertEquals("Корень_квадратный", simpleFunctionInterface.selectSimpleFunctionByFunctionCode("TEST_SQRT").split(" ")[2]);

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
        assertEquals("Корень_квадратный", simpleFunctionInterface.selectSimpleFunctionByFunctionCode("TEST_SQRT").split(" ")[2]);
        assertEquals("Степень", simpleFunctionInterface.selectSimpleFunctionByFunctionCode("TEST_POW").split(" ")[2]);
    }
}