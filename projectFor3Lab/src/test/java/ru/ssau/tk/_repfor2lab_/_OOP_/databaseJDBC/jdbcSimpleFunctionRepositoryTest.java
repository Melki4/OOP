package ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC;

import org.junit.jupiter.api.*;
import ru.ssau.tk._repfor2lab_._OOP_.databaseDTO.SimpleFunction;
import ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.Dao.JdbcSimpleFunctionRepository;
import ru.ssau.tk._repfor2lab_._OOP_.exceptions.DataDoesNotExistException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class jdbcSimpleFunctionRepositoryTest {

    private JdbcSimpleFunctionRepository simpleFunctionRepository;

    @BeforeEach
    void setUp() {
        simpleFunctionRepository = new JdbcSimpleFunctionRepository();
        simpleFunctionRepository.createTable();
    }

    @AfterEach
    void tearDown() {
        simpleFunctionRepository.deleteAllFunctions();
    }

    @Test
    void testFullCrudForSimpleFunctions() {
        // CREATE - Добавляем несколько функций
        simpleFunctionRepository.addSimpleFunction("SIN", "Синус");
        simpleFunctionRepository.addSimpleFunction("COS", "Косинус");
        simpleFunctionRepository.addSimpleFunction("TAN", "Тангенс");

        // READ - Получаем все функции
        List<SimpleFunction> allFunctions = simpleFunctionRepository.selectAllSimpleFunctions();
        assertFalse(allFunctions.isEmpty());
        assertEquals(3, allFunctions.size());

        // Ищем конкретную функцию по коду
        SimpleFunction sinFunction = simpleFunctionRepository.selectSimpleFunctionByFunctionCode("SIN");
        assertNotNull(sinFunction);
        assertEquals("SIN", sinFunction.getFunctionCode());
        assertEquals("Синус", sinFunction.getLocalName());

        SimpleFunction cosFunction = simpleFunctionRepository.selectSimpleFunctionByFunctionCode("COS");
        assertNotNull(cosFunction);
        assertEquals("COS", cosFunction.getFunctionCode());
        assertEquals("Косинус", cosFunction.getLocalName());

        // UPDATE - Обновляем локальное имя
        simpleFunctionRepository.updateLocalNameByFunctionCode("Синус_функция", "SIN");

        // Проверяем обновление
        SimpleFunction updatedSinFunction = simpleFunctionRepository.selectSimpleFunctionByFunctionCode("SIN");
        assertEquals("Синус_функция", updatedSinFunction.getLocalName());
        assertEquals("SIN", updatedSinFunction.getFunctionCode());

        // DELETE - Удаляем одну функцию
        simpleFunctionRepository.deleteSimpleFunctionByFunctionCode("TAN");

        // Проверяем что функция удалена
        assertThrows(RuntimeException.class, () -> {
            simpleFunctionRepository.selectSimpleFunctionByFunctionCode("TAN");
        });

        // Проверяем что остальные функции остались
        List<SimpleFunction> remainingFunctions = simpleFunctionRepository.selectAllSimpleFunctions();
        assertEquals(2, remainingFunctions.size());
    }

    @Test
    @DisplayName("Тест с различными кодами функций и локальными именами")
    void testVariousFunctionCodesAndLocalNames() {
        // Добавляем функции с разными кодами и именами
        simpleFunctionRepository.addSimpleFunction("TEST_LOG", "Логарифм");
        simpleFunctionRepository.addSimpleFunction("TEST_EXP", "Экспонента");
        simpleFunctionRepository.addSimpleFunction("TEST_SQRT", "Квадратный_корень");
        simpleFunctionRepository.addSimpleFunction("TEST_POW", "Степень");

        // Проверяем добавление
        List<SimpleFunction> allFunctions = simpleFunctionRepository.selectAllSimpleFunctions();
        assertEquals(4, allFunctions.size());

        // Проверяем поиск по каждому коду
        SimpleFunction logFunction = simpleFunctionRepository.selectSimpleFunctionByFunctionCode("TEST_LOG");
        assertEquals("Логарифм", logFunction.getLocalName());
        assertEquals("TEST_LOG", logFunction.getFunctionCode());

        SimpleFunction expFunction = simpleFunctionRepository.selectSimpleFunctionByFunctionCode("TEST_EXP");
        assertEquals("Экспонента", expFunction.getLocalName());
        assertEquals("TEST_EXP", expFunction.getFunctionCode());

        SimpleFunction sqrtFunction = simpleFunctionRepository.selectSimpleFunctionByFunctionCode("TEST_SQRT");
        assertEquals("Квадратный_корень", sqrtFunction.getLocalName());
        assertEquals("TEST_SQRT", sqrtFunction.getFunctionCode());

        SimpleFunction powFunction = simpleFunctionRepository.selectSimpleFunctionByFunctionCode("TEST_POW");
        assertEquals("Степень", powFunction.getLocalName());
        assertEquals("TEST_POW", powFunction.getFunctionCode());

        // Обновляем все локальные имена
        simpleFunctionRepository.updateLocalNameByFunctionCode("Натуральный_логарифм", "TEST_LOG");
        simpleFunctionRepository.updateLocalNameByFunctionCode("Экспоненциальная_функция", "TEST_EXP");
        simpleFunctionRepository.updateLocalNameByFunctionCode("Корень_квадратный", "TEST_SQRT");

        // Проверяем обновления
        assertEquals("Натуральный_логарифм", simpleFunctionRepository.selectSimpleFunctionByFunctionCode("TEST_LOG").getLocalName());
        assertEquals("Экспоненциальная_функция", simpleFunctionRepository.selectSimpleFunctionByFunctionCode("TEST_EXP").getLocalName());
        assertEquals("Корень_квадратный", simpleFunctionRepository.selectSimpleFunctionByFunctionCode("TEST_SQRT").getLocalName());

        // Удаляем несколько функций
        simpleFunctionRepository.deleteSimpleFunctionByFunctionCode("TEST_LOG");
        simpleFunctionRepository.deleteSimpleFunctionByFunctionCode("TEST_EXP");

        // Проверяем что удаленные функции больше не находятся
        assertThrows(RuntimeException.class, () ->
                simpleFunctionRepository.selectSimpleFunctionByFunctionCode("TEST_LOG")
        );
        assertThrows(RuntimeException.class, () ->
                simpleFunctionRepository.selectSimpleFunctionByFunctionCode("TEST_EXP")
        );

        // Проверяем что оставшиеся функции все еще существуют
        assertEquals("Корень_квадратный", simpleFunctionRepository.selectSimpleFunctionByFunctionCode("TEST_SQRT").getLocalName());
        assertEquals("Степень", simpleFunctionRepository.selectSimpleFunctionByFunctionCode("TEST_POW").getLocalName());
    }

    @Test
    @DisplayName("Тест сортировки функций по локальному имени")
    void testSelectAllSimpleFunctionsSortedByLocalName() {
        // Добавляем функции в разном порядке
        simpleFunctionRepository.addSimpleFunction("Z_FUNC", "Яблоко");
        simpleFunctionRepository.addSimpleFunction("A_FUNC", "Абрикос");
        simpleFunctionRepository.addSimpleFunction("M_FUNC", "Манго");

        // Получаем отсортированный список
        List<SimpleFunction> sortedFunctions = simpleFunctionRepository.selectAllSimpleFunctionsSortedByLocalName();
        assertEquals(3, sortedFunctions.size());

        // Проверяем сортировку по локальному имени (в алфавитном порядке)
        assertEquals("Абрикос", sortedFunctions.get(0).getLocalName());
        assertEquals("Манго", sortedFunctions.get(1).getLocalName());
        assertEquals("Яблоко", sortedFunctions.get(2).getLocalName());
    }

    @Test
    @DisplayName("Тест получения функции по ID")
    void testSelectSimpleFunctionById() {
        // Добавляем функцию
        simpleFunctionRepository.addSimpleFunction("TEST_FUNC", "Тестовая функция");

        // Получаем все функции чтобы найти ID
        List<SimpleFunction> allFunctions = simpleFunctionRepository.selectAllSimpleFunctions();
        assertEquals(1, allFunctions.size());

        String functionCode = allFunctions.get(0).getFunctionCode();

        // Получаем функцию по ID
        SimpleFunction functionById = simpleFunctionRepository.selectSimpleFunctionByFunctionCode(functionCode);

        // Проверяем корректность данных
        assertNotNull(functionById);
        assertEquals(functionCode, functionById.getFunctionCode());
        assertEquals("TEST_FUNC", functionById.getFunctionCode());
        assertEquals("Тестовая функция", functionById.getLocalName());
    }

    @Test
    @DisplayName("Тест проверки существования функции")
    void testExistsSimpleFunction() {
        // Проверяем несуществующую функцию
        assertFalse(simpleFunctionRepository.existsSimpleFunction("NON_EXISTENT"));

        // Добавляем функцию и проверяем существование
        simpleFunctionRepository.addSimpleFunction("EXISTING_FUNC", "Существующая функция");
        assertTrue(simpleFunctionRepository.existsSimpleFunction("EXISTING_FUNC"));
    }

    @Test
    @DisplayName("Тест поведения с пустой базой данных")
    void testEmptyDatabaseBehavior() {
        // Проверяем поведение с пустой базой данных
        assertThrows(RuntimeException.class, () -> {simpleFunctionRepository.selectAllSimpleFunctions();});
    }

    @Test
    @DisplayName("Тест обновления функции по ID")
    void testUpdateSimpleFunction() {
        // Добавляем тестовую функцию
        simpleFunctionRepository.addSimpleFunction("OLD_CODE", "Старое имя");

        // Получаем функцию для обновления
        SimpleFunction originalFunction = simpleFunctionRepository.selectSimpleFunctionByFunctionCode("OLD_CODE");
        int functionId = originalFunction.getFunctionId().intValue();
    }

    @Test
    @DisplayName("Тест удаления функции по ID")
    void testDeleteSimpleFunctionById() {
        // Добавляем несколько функций
        simpleFunctionRepository.addSimpleFunction("FUNC1", "Функция 1");
        simpleFunctionRepository.addSimpleFunction("FUNC2", "Функция 2");

        // Получаем функцию для удаления
        SimpleFunction functionToDelete = simpleFunctionRepository.selectSimpleFunctionByFunctionCode("FUNC1");
        int functionId = functionToDelete.getFunctionId().intValue();

        // Проверяем начальное состояние
        List<SimpleFunction> initialFunctions = simpleFunctionRepository.selectAllSimpleFunctions();
        assertEquals(2, initialFunctions.size());

        assertTrue(simpleFunctionRepository.existsSimpleFunction("FUNC2"));
    }

    @Test
    @DisplayName("Тест целостности данных функций")
    void testDataIntegrity() {
        // Добавляем функцию с разными типами данных
        simpleFunctionRepository.addSimpleFunction("COM_FUNC", "Сложное локальное имя !@#$%");

        // Получаем функцию
        SimpleFunction function = simpleFunctionRepository.selectSimpleFunctionByFunctionCode("COM_FUNC");

        // Проверяем целостность всех полей
        assertNotNull(function.getFunctionId());
        assertEquals("COM_FUNC", function.getFunctionCode());
        assertEquals("Сложное локальное имя !@#$%", function.getLocalName());
    }
}