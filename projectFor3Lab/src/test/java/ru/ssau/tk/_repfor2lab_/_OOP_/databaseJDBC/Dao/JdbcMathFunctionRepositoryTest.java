package ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.Dao;

import org.junit.jupiter.api.*;
import ru.ssau.tk._repfor2lab_._OOP_.databaseEnteties.MathFunctions;
import ru.ssau.tk._repfor2lab_._OOP_.exceptions.DataDoesNotExistException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class JdbcMathFunctionRepositoryTest {

    @BeforeAll
    static void creation(){
        JdbcUserRepository userRepository = new JdbcUserRepository();
        userRepository.createTable();
        userRepository.createUser("array", "vovapain", "vivatVOVA", "user");
        userRepository.createUser("list", "yojo", "vivatyojo", "admin");

        JdbcSimpleFunctionRepository simpleFunctionRepository= new JdbcSimpleFunctionRepository();
        simpleFunctionRepository.createSimpleFunction("Квадратичная функция");
        simpleFunctionRepository.createSimpleFunction("Табулированная функция");

        JdbcMathFunctionRepository mathFunctionRepository1 = new JdbcMathFunctionRepository();
        mathFunctionRepository1.createMathFunction("x^2", 100, -1,
                13, userRepository.selectIdByLogin("yojo"), "Квадратичная функция");
        mathFunctionRepository1.createMathFunction("x^2", 1000, -1,
                13, userRepository.selectIdByLogin("yojo"), "Квадратичная функция");
        mathFunctionRepository1.createMathFunction("e^(x^2-1)", 100, -1,
                123, userRepository.selectIdByLogin("vovapain"), "Табулированная функция");
    }

    @AfterAll
    static void cleaning(){
        JdbcUserRepository userRepository = new JdbcUserRepository();
        userRepository.deleteAllUsers();
        JdbcMathFunctionRepository mathFunctionRepository = new JdbcMathFunctionRepository();
        mathFunctionRepository.deleteAllFunctions();
        JdbcSimpleFunctionRepository simpleFunctionRepository = new JdbcSimpleFunctionRepository();
        simpleFunctionRepository.deleteAllFunctions();
    }

    @Order(1)
    @Test
    void findMathFunctionsByUserId() {
        JdbcUserRepository userRepository = new JdbcUserRepository();
        JdbcMathFunctionRepository mathFunctionRepository = new JdbcMathFunctionRepository();
        List<MathFunctions> list = mathFunctionRepository.findMathFunctionsByUserId(userRepository.selectIdByLogin("vovapain"));
        assertEquals("e^(x^2-1)", list.getFirst().getFunctionName());

    }

    @Order(2)
    @Test
    void findMathFunctionsByName() {
        JdbcMathFunctionRepository mathFunctionRepository = new JdbcMathFunctionRepository();
        List<MathFunctions> list = mathFunctionRepository.findMathFunctionsByName("x^2");
        assertEquals(2, list.size());
    }

    @Order(3)
    @Test
    void findMathFunctionComplex() {
        JdbcMathFunctionRepository mathFunctionRepository = new JdbcMathFunctionRepository();
        assertEquals("x^2", mathFunctionRepository.findMathFunctionComplex(-1, 13,
                100, "x^2").getFunctionName());
    }

    @Order(4)
    @Test
    void updateFunctionNameByFunctionId() {
        JdbcMathFunctionRepository mathFunctionRepository = new JdbcMathFunctionRepository();
        mathFunctionRepository.updateFunctionNameByFunctionId("que", mathFunctionRepository.findMathFunctionComplex(-1, 13,
                100, "x^2").getFunctionId());
        if( mathFunctionRepository.existsFunctionComplex(-1, 13,
                100, "que")){
            assertEquals("que", mathFunctionRepository.findMathFunctionComplex(-1, 13,
                    100, "que").getFunctionName());

        }
    }

    @Order(5)
    @Test
    void deleteMathFunctionByFunctionId() {
        JdbcUserRepository userRepository = new JdbcUserRepository();
        JdbcMathFunctionRepository mathFunctionRepository = new JdbcMathFunctionRepository();

        mathFunctionRepository.createMathFunction("e^(x^2-1)", 1100, -1,
                123, userRepository.selectIdByLogin("vovapain"), "Табулированная функция");

        mathFunctionRepository.deleteMathFunctionsByUserId(userRepository.selectIdByLogin("vovapain"));
        mathFunctionRepository.createMathFunction("e^(x^2-1)", 1100, -1,
                123, userRepository.selectIdByLogin("vovapain"), "Табулированная функция");

        assertEquals(1, mathFunctionRepository.findMathFunctionsByUserId(userRepository.selectIdByLogin("vovapain")).size());
    }

    @Order(6)
    @Test
    void deleteMathFunctionsByUserId() {
        JdbcUserRepository userRepository = new JdbcUserRepository();
        JdbcMathFunctionRepository mathFunctionRepository = new JdbcMathFunctionRepository();
        mathFunctionRepository.deleteMathFunctionsByUserId(userRepository.selectIdByLogin("yojo"));
        assertThrows(DataDoesNotExistException.class, ()->mathFunctionRepository.findMathFunctionsByUserId(userRepository.selectIdByLogin("yojo")).size());
    }
}