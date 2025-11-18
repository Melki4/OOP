package ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.ssau.tk._repfor2lab_._OOP_.databaseDTO.SimpleFunction;
import ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.Dao.JdbcSimpleFunctionRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class jdbcSimpleFunctionRepositoryDTOTest {
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
    void testSelectAllSimpleFunctionsAsDTO() {
        // Добавляем тестовые данные
        simpleFunctionRepository.addSimpleFunction("TEST_LOG", "Логарифм");
        simpleFunctionRepository.addSimpleFunction("TEST_EXP", "Экспонента");
        simpleFunctionRepository.addSimpleFunction("TEST_SQRT", "Квадратный корень");

        // Получаем все функции как DTO
        List<SimpleFunction> simpleFunctions = simpleFunctionRepository.selectAllSimpleFunctions();

        // Проверяем результат
        assertEquals(3, simpleFunctions.size());

        // Проверяем корректность данных для каждой функции
        for (SimpleFunction function : simpleFunctions) {
            assertNotNull(function.getFunctionId());
            assertNotNull(function.getFunctionCode());
            assertNotNull(function.getLocalName());

            // Проверяем конкретные значения
            switch (function.getFunctionCode()) {
                case "TEST_LOG":
                    assertEquals("Логарифм", function.getLocalName());
                    break;
                case "TEST_EXP":
                    assertEquals("Экспонента", function.getLocalName());
                    break;
                case "TEST_SQRT":
                    assertEquals("Квадратный корень", function.getLocalName());
                    break;
            }
        }
    }

    @Test
    void testSelectSimpleFunctionByFunctionCode() {
        // Добавляем тестовые данные
        simpleFunctionRepository.addSimpleFunction("TEST_LOG", "Логарифм");
        simpleFunctionRepository.addSimpleFunction("TEST_EXP", "Экспонента");
        simpleFunctionRepository.addSimpleFunction("TEST_SQRT", "Квадратный_корень");
        simpleFunctionRepository.addSimpleFunction("TESTSQRT", "Квадратный корень");

        // Получаем функцию по коду как DTO
        SimpleFunction simpleFunctionDTO = simpleFunctionRepository.selectSimpleFunctionByFunctionCode("TEST_EXP");

        // Проверяем корректность данных
        assertNotNull(simpleFunctionDTO);
        assertEquals("TEST_EXP", simpleFunctionDTO.getFunctionCode());
        assertEquals("Экспонента", simpleFunctionDTO.getLocalName());
        assertNotNull(simpleFunctionDTO.getFunctionId());

        // Проверяем функцию с пробелом в локальном имени
        SimpleFunction simpleFunctionWithSpace = simpleFunctionRepository.selectSimpleFunctionByFunctionCode("TESTSQRT");
        assertNotNull(simpleFunctionWithSpace);
        assertEquals("TESTSQRT", simpleFunctionWithSpace.getFunctionCode());
        assertEquals("Квадратный корень", simpleFunctionWithSpace.getLocalName());
    }

    @Test
    void testSelectSimpleFunctionById() {
        // Добавляем тестовую функцию
        simpleFunctionRepository.addSimpleFunction("TEST_FUNC", "Тестовая функция");

        // Получаем все функции чтобы найти ID
        List<SimpleFunction> allFunctions = simpleFunctionRepository.selectAllSimpleFunctions();
        assertEquals(1, allFunctions.size());

        String functionCode = allFunctions.get(0).getFunctionCode();

        // Получаем функцию по ID
        SimpleFunction functionByCode = simpleFunctionRepository.selectSimpleFunctionByFunctionCode(functionCode);

        // Проверяем корректность данных
        assertNotNull(functionByCode);
        assertEquals(functionCode, functionByCode.getFunctionCode());
        assertEquals("TEST_FUNC", functionByCode.getFunctionCode());
        assertEquals("Тестовая функция", functionByCode.getLocalName());
    }

    @Test
    void testExistsSimpleFunction() {
        // Проверяем несуществующую функцию
        assertFalse(simpleFunctionRepository.existsSimpleFunction("NON_EXISTENT"));

        // Добавляем функцию и проверяем существование
        simpleFunctionRepository.addSimpleFunction("EXISTING_FUNC", "Существующая функция");
        assertTrue(simpleFunctionRepository.existsSimpleFunction("EXISTING_FUNC"));
    }

    @Test
    void testEmptyDatabase() {
        // Проверяем поведение с пустой базой данных
        assertThrows(RuntimeException.class, () -> {simpleFunctionRepository.selectAllSimpleFunctions();});
    }

    @Test
    void testUpdateSimpleFunction() {
        // Добавляем тестовую функцию
        simpleFunctionRepository.addSimpleFunction("OLD_CODE", "Старое имя");

        // Получаем функцию для обновления
        SimpleFunction originalFunction = simpleFunctionRepository.selectSimpleFunctionByFunctionCode("OLD_CODE");
        String functionId = originalFunction.getFunctionCode();

        // Обновляем функцию
        simpleFunctionRepository.updateLocalNameByFunctionCode("Новое имя", "OLD_CODE");

        // Проверяем обновление
        SimpleFunction updatedFunction = simpleFunctionRepository.selectSimpleFunctionByFunctionCode(functionId);
        assertEquals("Новое имя", updatedFunction.getLocalName());
    }

    @Test
    void testDeleteSimpleFunction() {
        // Добавляем несколько функций
        simpleFunctionRepository.addSimpleFunction("FUNC1", "Функция 1");
        simpleFunctionRepository.addSimpleFunction("FUNC2", "Функция 2");
        simpleFunctionRepository.addSimpleFunction("FUNC3", "Функция 3");

        // Получаем функцию для удаления
        SimpleFunction functionToDelete = simpleFunctionRepository.selectSimpleFunctionByFunctionCode("FUNC2");
        int functionId = functionToDelete.getFunctionId().intValue();

        // Проверяем начальное состояние
        List<SimpleFunction> initialFunctions = simpleFunctionRepository.selectAllSimpleFunctions();
        assertEquals(3, initialFunctions.size());
    }

    @Test
    void testDataIntegrity() {
        // Добавляем функцию с разными типами данных
        simpleFunctionRepository.addSimpleFunction("COM_FUNC", "Очень сложное локальное имя");

        // Получаем функцию
        SimpleFunction function = simpleFunctionRepository.selectSimpleFunctionByFunctionCode("COM_FUNC");

        // Проверяем целостность всех полей
        assertNotNull(function.getFunctionId());
        assertEquals("COM_FUNC", function.getFunctionCode());
        assertEquals("Очень сложное локальное имя", function.getLocalName());
    }
}