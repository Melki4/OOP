package ru.ssau.tk._repfor2lab_._OOP_.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.tk._repfor2lab_._OOP_.Application;
import ru.ssau.tk._repfor2lab_._OOP_.entities.MathFunctions;
import ru.ssau.tk._repfor2lab_._OOP_.entities.Users;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = Application.class)
@Transactional
@Rollback
class MathFunctionsRepositoriesTest {

    @Autowired
    private MathFunctionsRepositories mathFunctionsRepository;

    @Autowired
    private UsersRepositories usersRepository;

    private Users testUser;
    private MathFunctions testFunction1;
    private MathFunctions testFunction2;
    private MathFunctions testFunction3;

    @BeforeEach
    void setUp() {
        // Очистка базы данных перед каждым тестом
        mathFunctionsRepository.deleteAll();
        usersRepository.deleteAll();

        // Создание тестового пользователя
        testUser = new Users();
        testUser.setLogin("test_user");
        testUser.setPassword("password123");
        testUser.setFactoryType("ARRAY");
        testUser.setRole("USER");
        testUser = usersRepository.save(testUser);

        // Создание тестовых математических функций
        testFunction1 = new MathFunctions();
        testFunction1.setNameOfFunction("sin(x)");
        testFunction1.setLeftBoarder(0.0);
        testFunction1.setRightBoarder(6.28);
        testFunction1.setAmountOfDots(100L);
        testFunction1.setUsers(testUser);

        testFunction2 = new MathFunctions();
        testFunction2.setNameOfFunction("cos(x)");
        testFunction2.setLeftBoarder(-3.14);
        testFunction2.setRightBoarder(3.14);
        testFunction2.setAmountOfDots(50L);
        testFunction2.setUsers(testUser);

        testFunction3 = new MathFunctions();
        testFunction3.setNameOfFunction("x^2");
        testFunction3.setLeftBoarder(-10.0);
        testFunction3.setRightBoarder(10.0);
        testFunction3.setAmountOfDots(200L);
        testFunction3.setUsers(testUser);

        // Сохранение тестовых данных
        mathFunctionsRepository.save(testFunction1);
        mathFunctionsRepository.save(testFunction2);
        mathFunctionsRepository.save(testFunction3);
    }

    @Test
    void testSaveMathFunction() {
        MathFunctions newFunction = new MathFunctions();
        newFunction.setNameOfFunction("exp(x)");
        newFunction.setLeftBoarder(0.0);
        newFunction.setRightBoarder(5.0);
        newFunction.setAmountOfDots(75L);
        newFunction.setUsers(testUser);

        MathFunctions savedFunction = mathFunctionsRepository.save(newFunction);

        assertNotNull(savedFunction.getMathFunctionsID());
        assertEquals("exp(x)", savedFunction.getNameOfFunction());
        assertEquals(0.0, savedFunction.getLeftBoarder());
        assertEquals(5.0, savedFunction.getRightBoarder());
        assertEquals(75L, savedFunction.getAmountOfDots());
        assertEquals(testUser.getUserID(), savedFunction.getUsers().getUserID());
    }


    @Test
    void testFindByNameOfFunction() {
        Optional<MathFunctions> foundFunction = mathFunctionsRepository.findByNameOfFunction("cos(x)");

        assertTrue(foundFunction.isPresent());
        assertEquals(-3.14, foundFunction.get().getLeftBoarder());
        assertEquals(3.14, foundFunction.get().getRightBoarder());
        assertEquals(50L, foundFunction.get().getAmountOfDots());
    }

    @Test
    void testFindByNameOfFunction_NotFound() {
        Optional<MathFunctions> foundFunction = mathFunctionsRepository.findByNameOfFunction("non_existent_function");

        assertFalse(foundFunction.isPresent());
    }

    @Test
    void testExistsByNameOfFunction() {
        boolean exists = mathFunctionsRepository.existsByNameOfFunction("sin(x)");
        boolean notExists = mathFunctionsRepository.existsByNameOfFunction("non_existent_function");

        assertTrue(exists);
        assertFalse(notExists);
    }

    @Test
    void testFindByUsersUserID() {
        Long userId = testUser.getUserID();

        List<MathFunctions> userFunctions = mathFunctionsRepository.findByUsersUserID(userId);

        assertEquals(3, userFunctions.size());
        assertTrue(userFunctions.stream().anyMatch(func -> "sin(x)".equals(func.getNameOfFunction())));
        assertTrue(userFunctions.stream().anyMatch(func -> "cos(x)".equals(func.getNameOfFunction())));
        assertTrue(userFunctions.stream().anyMatch(func -> "x^2".equals(func.getNameOfFunction())));
    }

    @Test
    void testExistsByUsersUserID() {
        Long userId = testUser.getUserID();
        Long nonExistentUserId = 999L;

        boolean exists = mathFunctionsRepository.existsByUsersUserID(userId);
        boolean notExists = mathFunctionsRepository.existsByUsersUserID(nonExistentUserId);

        assertTrue(exists);
        assertFalse(notExists);
    }

    @Test
    void testFindByLeftBoarderBetween() {
        List<MathFunctions> functions = mathFunctionsRepository.findByLeftBoarderBetween(-5.0, 1.0);

        assertEquals(2, functions.size()); // sin(x) и cos(x)
        assertTrue(functions.stream().anyMatch(func -> "sin(x)".equals(func.getNameOfFunction())));
        assertTrue(functions.stream().anyMatch(func -> "cos(x)".equals(func.getNameOfFunction())));
    }

    @Test
    void testFindByRightBoarderBetween() {
        List<MathFunctions> functions = mathFunctionsRepository.findByRightBoarderBetween(5.0, 15.0);

        assertEquals(2, functions.size()); // sin(x) и x^2
        assertTrue(functions.stream().anyMatch(func -> "sin(x)".equals(func.getNameOfFunction())));
        assertTrue(functions.stream().anyMatch(func -> "x^2".equals(func.getNameOfFunction())));
    }

    @Test
    void testFindByAmountOfDotsBetween() {
        List<MathFunctions> functions = mathFunctionsRepository.findByAmountOfDotsBetween(80L, 250L);

        assertEquals(2, functions.size()); // sin(x) и x^2
        assertTrue(functions.stream().anyMatch(func -> "sin(x)".equals(func.getNameOfFunction())));
        assertTrue(functions.stream().anyMatch(func -> "x^2".equals(func.getNameOfFunction())));
    }


    @Test
    void testDeleteByNameOfFunction() {
        // Проверяем, что функция существует
        assertTrue(mathFunctionsRepository.existsByNameOfFunction("x^2"));

        // Удаляем по имени функции
        mathFunctionsRepository.deleteByNameOfFunction("x^2");

        // Проверяем, что функция удалена
        assertFalse(mathFunctionsRepository.existsByNameOfFunction("x^2"));

        // Проверяем, что другие функции остались
        assertTrue(mathFunctionsRepository.existsByNameOfFunction("sin(x)"));
        assertTrue(mathFunctionsRepository.existsByNameOfFunction("cos(x)"));
    }

    @Test
    void testFindAllMathFunctions() {
        List<MathFunctions> allFunctions = mathFunctionsRepository.findAll();

        assertEquals(3, allFunctions.size());
        assertTrue(allFunctions.stream().anyMatch(func -> "sin(x)".equals(func.getNameOfFunction())));
        assertTrue(allFunctions.stream().anyMatch(func -> "cos(x)".equals(func.getNameOfFunction())));
        assertTrue(allFunctions.stream().anyMatch(func -> "x^2".equals(func.getNameOfFunction())));
    }


    @Test
    void testCountMathFunctions() {
        long count = mathFunctionsRepository.count();
        assertEquals(3, count);

        // Добавляем новую функцию и проверяем счетчик
        MathFunctions newFunction = new MathFunctions();
        newFunction.setNameOfFunction("ln(x)");
        newFunction.setLeftBoarder(0.1);
        newFunction.setRightBoarder(10.0);
        newFunction.setAmountOfDots(80L);
        newFunction.setUsers(testUser);

        mathFunctionsRepository.save(newFunction);

        long newCount = mathFunctionsRepository.count();
        assertEquals(4, newCount);
    }

    @Test
    void testDeleteById() {
        Long functionId = testFunction1.getMathFunctionsID();

        // Проверяем, что функция существует
        assertTrue(mathFunctionsRepository.findById(functionId).isPresent());

        // Удаляем по ID
        mathFunctionsRepository.deleteById(functionId);

        // Проверяем, что функция удалена
        assertFalse(mathFunctionsRepository.findById(functionId).isPresent());
    }

    @Test
    void testSaveAndFlush() {
        MathFunctions newFunction = new MathFunctions();
        newFunction.setNameOfFunction("flush_function");
        newFunction.setLeftBoarder(1.0);
        newFunction.setRightBoarder(2.0);
        newFunction.setAmountOfDots(30L);
        newFunction.setUsers(testUser);

        MathFunctions savedFunction = mathFunctionsRepository.saveAndFlush(newFunction);

        assertNotNull(savedFunction.getMathFunctionsID());
        assertEquals("flush_function", savedFunction.getNameOfFunction());

        // Немедленно проверяем, что функция доступна
        Optional<MathFunctions> foundFunction = mathFunctionsRepository.findByNameOfFunction("flush_function");
        assertTrue(foundFunction.isPresent());
    }

    @Test
    void testFindAllById() {
        Long id1 = testFunction1.getMathFunctionsID();
        Long id2 = testFunction2.getMathFunctionsID();

        List<MathFunctions> functions = mathFunctionsRepository.findAllById(List.of(id1, id2));

        assertEquals(2, functions.size());
        assertTrue(functions.stream().anyMatch(func -> func.getMathFunctionsID().equals(id1)));
        assertTrue(functions.stream().anyMatch(func -> func.getMathFunctionsID().equals(id2)));
    }

    @Test
    void testFindByUsersUserID_NoFunctions() {
        // Создаем нового пользователя без функций
        Users newUser = new Users();
        newUser.setLogin("new_user");
        newUser.setPassword("password456");
        newUser.setFactoryType("LINKED_LIST");
        newUser.setRole("USER");
        newUser = usersRepository.save(newUser);

        List<MathFunctions> functions = mathFunctionsRepository.findByUsersUserID(newUser.getUserID());

        assertTrue(functions.isEmpty());
    }

}