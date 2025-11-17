package ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC;

import org.junit.jupiter.api.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class jdbcSimpleFunctionRepositoryTest {

    private JdbcSimpleFunctionRepository JdbcSimpleFunctionRepository;

    @BeforeEach
    void setUp() {
        var s = new JdbcSimpleFunctionRepository();
        s.deleteAllFunctions();
        JdbcSimpleFunctionRepository = new JdbcSimpleFunctionRepository();
        JdbcSimpleFunctionRepository.createTable();
    }

    @AfterEach
    void tearDown() {
        // Очищаем тестовые данные
        var s = new JdbcSimpleFunctionRepository();
        s.deleteAllFunctions();
    }

    @Test
    void testFullCrudForSimpleFunctions() {
        // CREATE - Добавляем несколько функций
        JdbcSimpleFunctionRepository.addSimpleFunction("SIN", "Синус");
        JdbcSimpleFunctionRepository.addSimpleFunction("COS", "Косинус");
        JdbcSimpleFunctionRepository.addSimpleFunction("TAN", "Тангенс");

        // READ - Получаем все функции
        List<String> allFunctions = JdbcSimpleFunctionRepository.selectAllSimpleFunctionsSortedByLocalName();
        assertFalse(allFunctions.isEmpty());

        // Ищем конкретную функцию по коду
        String sinLocalName = JdbcSimpleFunctionRepository.selectSimpleFunctionByFunctionCode("SIN");
        assertNotNull(sinLocalName);
        assertEquals("Синус", sinLocalName.split(" ")[2]);

        String cosLocalName = JdbcSimpleFunctionRepository.selectSimpleFunctionByFunctionCode("COS");
        assertNotNull(cosLocalName);
        assertEquals("Косинус", cosLocalName.split(" ")[2]);

        // UPDATE - Обновляем локальное имя
        JdbcSimpleFunctionRepository.updateLocalNameByFunctionCode("Синус_функция", "SIN");

        // Проверяем обновление
        String updatedSinLocalName = JdbcSimpleFunctionRepository.selectSimpleFunctionByFunctionCode("SIN");
        assertEquals("Синус_функция", updatedSinLocalName.split(" ")[2]);

        // DELETE - Удаляем одну функцию
        JdbcSimpleFunctionRepository.deleteSimpleFunctionByFunctionCode("TAN");

        // Проверяем что функция удалена
        assertThrows(RuntimeException.class, () -> {
            JdbcSimpleFunctionRepository.selectSimpleFunctionByFunctionCode("TAN");
        });
    }

    @Test
    @DisplayName("Тест с различными кодами функций и локальными именами")
    void testVariousFunctionCodesAndLocalNames() {
        // Добавляем функции с разными кодами и именами
        JdbcSimpleFunctionRepository.addSimpleFunction("TEST_LOG", "Логарифм");
        JdbcSimpleFunctionRepository.addSimpleFunction("TEST_EXP", "Экспонента");
        JdbcSimpleFunctionRepository.addSimpleFunction("TEST_SQRT", "Квадратный_корень");
        JdbcSimpleFunctionRepository.addSimpleFunction("TEST_POW", "Степень");

        // Проверяем добавление
        List<String> allFunctions = JdbcSimpleFunctionRepository.selectAllSimpleFunctions();
        assertTrue(allFunctions.size() >= 4);

        // Проверяем поиск по каждому коду
        assertEquals("Логарифм", JdbcSimpleFunctionRepository.selectSimpleFunctionByFunctionCode("TEST_LOG").split(" ")[2]);
        assertEquals("Экспонента", JdbcSimpleFunctionRepository.selectSimpleFunctionByFunctionCode("TEST_EXP").split(" ")[2]);
        assertEquals("Квадратный_корень", JdbcSimpleFunctionRepository.selectSimpleFunctionByFunctionCode("TEST_SQRT").split(" ")[2]);
        assertEquals("Степень", JdbcSimpleFunctionRepository.selectSimpleFunctionByFunctionCode("TEST_POW").split(" ")[2]);

        // Обновляем все локальные имена
        JdbcSimpleFunctionRepository.updateLocalNameByFunctionCode("Натуральный_логарифм", "TEST_LOG");
        JdbcSimpleFunctionRepository.updateLocalNameByFunctionCode("Экспоненциальная_функция", "TEST_EXP");
        JdbcSimpleFunctionRepository.updateLocalNameByFunctionCode("Корень_квадратный", "TEST_SQRT");

        // Проверяем обновления
        assertEquals("Натуральный_логарифм", JdbcSimpleFunctionRepository.selectSimpleFunctionByFunctionCode("TEST_LOG").split(" ")[2]);
        assertEquals("Экспоненциальная_функция", JdbcSimpleFunctionRepository.selectSimpleFunctionByFunctionCode("TEST_EXP").split(" ")[2]);
        assertEquals("Корень_квадратный", JdbcSimpleFunctionRepository.selectSimpleFunctionByFunctionCode("TEST_SQRT").split(" ")[2]);

        // Удаляем несколько функций
        JdbcSimpleFunctionRepository.deleteSimpleFunctionByFunctionCode("TEST_LOG");
        JdbcSimpleFunctionRepository.deleteSimpleFunctionByFunctionCode("TEST_EXP");

        // Проверяем что удаленные функции больше не находятся
        assertThrows(RuntimeException.class, () ->
                JdbcSimpleFunctionRepository.selectSimpleFunctionByFunctionCode("TEST_LOG")
        );
        assertThrows(RuntimeException.class, () ->
                JdbcSimpleFunctionRepository.selectSimpleFunctionByFunctionCode("TEST_EXP")
        );

        // Проверяем что оставшиеся функции все еще существуют
        assertEquals("Корень_квадратный", JdbcSimpleFunctionRepository.selectSimpleFunctionByFunctionCode("TEST_SQRT").split(" ")[2]);
        assertEquals("Степень", JdbcSimpleFunctionRepository.selectSimpleFunctionByFunctionCode("TEST_POW").split(" ")[2]);
    }
}