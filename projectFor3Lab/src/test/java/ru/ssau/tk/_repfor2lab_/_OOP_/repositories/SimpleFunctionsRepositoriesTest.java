package ru.ssau.tk._repfor2lab_._OOP_.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.tk._repfor2lab_._OOP_.config.AppConfig;
import ru.ssau.tk._repfor2lab_._OOP_.entities.SimpleFunctions;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(AppConfig.class)
@Transactional
class SimpleFunctionsRepositoriesTest {

    @Autowired
    private SimpleFunctionsRepositories simpleFunctionsRepository;

    private SimpleFunctions testFunction1;
    private SimpleFunctions testFunction2;

    @BeforeEach
    void setUp() {
        // Очистка базы данных перед каждым тестом
        simpleFunctionsRepository.deleteAll();

        // Создание тестовых функций
        testFunction1 = new SimpleFunctions("SIN", "sin_function");
        testFunction2 = new SimpleFunctions("COS", "cos_function");

        // Сохранение тестовых данных
        simpleFunctionsRepository.save(testFunction1);
        simpleFunctionsRepository.save(testFunction2);
    }

    // Тесты для метода findByLocalName

    @Test
    void testFindByLocalName_WhenFunctionExists_ShouldReturnFunction() {
        // When
        Optional<SimpleFunctions> foundFunction = simpleFunctionsRepository.findByLocalName("sin_function");

        // Then
        assertTrue(foundFunction.isPresent());
        assertEquals("sin_function", foundFunction.get().getLocalName());
        assertEquals("SIN", foundFunction.get().getFunctionCode());
    }

    @Test
    void testFindByLocalName_WhenFunctionNotExists_ShouldReturnEmpty() {
        // When
        Optional<SimpleFunctions> foundFunction = simpleFunctionsRepository.findByLocalName("non_existent_function");

        // Then
        assertFalse(foundFunction.isPresent());
    }

    @Test
    void testFindByLocalName_WithNull_ShouldReturnEmpty() {
        // When
        Optional<SimpleFunctions> foundFunction = simpleFunctionsRepository.findByLocalName(null);

        // Then
        assertFalse(foundFunction.isPresent());
    }

    @Test
    void testFindByLocalName_CaseSensitive_ShouldBeCaseSensitive() {
        // When
        Optional<SimpleFunctions> foundFunction = simpleFunctionsRepository.findByLocalName("SIN_FUNCTION");

        // Then
        assertFalse(foundFunction.isPresent(), "Поиск должен быть чувствителен к регистру");
    }

    // Тесты для метода existsByLocalName

    @Test
    void testExistsByLocalName_WhenFunctionExists_ShouldReturnTrue() {
        // When
        boolean exists = simpleFunctionsRepository.existsByLocalName("sin_function");

        // Then
        assertTrue(exists);
    }

    @Test
    void testExistsByLocalName_WhenFunctionNotExists_ShouldReturnFalse() {
        // When
        boolean exists = simpleFunctionsRepository.existsByLocalName("non_existent_function");

        // Then
        assertFalse(exists);
    }

    @Test
    void testExistsByLocalName_WithNull_ShouldReturnFalse() {
        // When
        boolean exists = simpleFunctionsRepository.existsByLocalName(null);

        // Then
        assertFalse(exists);
    }

    @Test
    void testExistsByLocalName_AfterSave_ShouldReturnTrue() {
        // Given
        SimpleFunctions newFunction = new SimpleFunctions("TAN", "tan_function");

        // When
        simpleFunctionsRepository.save(newFunction);
        boolean exists = simpleFunctionsRepository.existsByLocalName("tan_function");

        // Then
        assertTrue(exists);
    }

    // Тесты для метода deleteByLocalName

    @Test
    void testDeleteByLocalName_WhenFunctionExists_ShouldDeleteFunction() {
        // Given
        assertTrue(simpleFunctionsRepository.existsByLocalName("sin_function"));

        // When
        simpleFunctionsRepository.deleteByLocalName("sin_function");

        // Then
        assertFalse(simpleFunctionsRepository.existsByLocalName("sin_function"));
        assertFalse(simpleFunctionsRepository.findByLocalName("sin_function").isPresent());
    }

    @Test
    void testDeleteByLocalName_WhenFunctionNotExists_ShouldDoNothing() {
        // Given
        long initialCount = simpleFunctionsRepository.count();

        // When
        simpleFunctionsRepository.deleteByLocalName("non_existent_function");

        // Then
        assertEquals(initialCount, simpleFunctionsRepository.count());
    }

    @Test
    void testDeleteByLocalName_WithNull_ShouldDoNothing() {
        // Given
        long initialCount = simpleFunctionsRepository.count();

        // When
        simpleFunctionsRepository.deleteByLocalName(null);

        // Then
        assertEquals(initialCount, simpleFunctionsRepository.count());
    }

    @Test
    void testDeleteByLocalName_ShouldNotAffectOtherFunctions() {
        // Given
        assertTrue(simpleFunctionsRepository.existsByLocalName("sin_function"));
        assertTrue(simpleFunctionsRepository.existsByLocalName("cos_function"));

        // When
        simpleFunctionsRepository.deleteByLocalName("sin_function");

        // Then
        assertFalse(simpleFunctionsRepository.existsByLocalName("sin_function"));
        assertTrue(simpleFunctionsRepository.existsByLocalName("cos_function"));
    }

    // Дополнительные тесты для базовых методов JpaRepository

    @Test
    void testSave_NewFunction_ShouldPersistSuccessfully() {
        // Given
        SimpleFunctions newFunction = new SimpleFunctions("TAN", "tan_function");

        // When
        SimpleFunctions savedFunction = simpleFunctionsRepository.save(newFunction);

        // Then
        assertNotNull(savedFunction);
        assertEquals("tan_function", savedFunction.getLocalName());
        assertEquals("TAN", savedFunction.getFunctionCode());
        assertTrue(simpleFunctionsRepository.existsByLocalName("tan_function"));
    }

    @Test
    void testFindById_WhenFunctionExists_ShouldReturnFunction() {
        // Given
        String functionCode = testFunction1.getFunctionCode();

        // When
        Optional<SimpleFunctions> foundFunction = simpleFunctionsRepository.findById(functionCode);

        // Then
        assertTrue(foundFunction.isPresent());
        assertEquals("sin_function", foundFunction.get().getLocalName());
        assertEquals("SIN", foundFunction.get().getFunctionCode());
    }

    @Test
    void testFindAll_ShouldReturnAllFunctions() {
        // When
        var allFunctions = simpleFunctionsRepository.findAll();

        // Then
        assertEquals(2, allFunctions.size());
        assertTrue(allFunctions.stream().anyMatch(func -> "sin_function".equals(func.getLocalName())));
        assertTrue(allFunctions.stream().anyMatch(func -> "cos_function".equals(func.getLocalName())));
    }

    @Test
    void testCount_ShouldReturnCorrectCount() {
        // When
        long count = simpleFunctionsRepository.count();

        // Then
        assertEquals(2, count);
    }

    @Test
    void testDeleteById_ShouldDeleteFunction() {
        // Given
        String functionCode = testFunction1.getFunctionCode();
        assertTrue(simpleFunctionsRepository.findById(functionCode).isPresent());

        // When
        simpleFunctionsRepository.deleteById(functionCode);

        // Then
        assertFalse(simpleFunctionsRepository.findById(functionCode).isPresent());
    }


    @Test
    void testUpdate_Function_ShouldUpdateSuccessfully() {
        // Given
        SimpleFunctions function = simpleFunctionsRepository.findByLocalName("sin_function").get();
        function.setLocalName("updated_sin_function");

        // When
        SimpleFunctions updatedFunction = simpleFunctionsRepository.save(function);

        // Then
        assertEquals("updated_sin_function", updatedFunction.getLocalName());
        assertEquals("SIN", updatedFunction.getFunctionCode());

        // Проверяем, что данные сохранились в базе
        Optional<SimpleFunctions> persistedFunction = simpleFunctionsRepository.findByLocalName("updated_sin_function");
        assertTrue(persistedFunction.isPresent());
        assertEquals("SIN", persistedFunction.get().getFunctionCode());
    }
}