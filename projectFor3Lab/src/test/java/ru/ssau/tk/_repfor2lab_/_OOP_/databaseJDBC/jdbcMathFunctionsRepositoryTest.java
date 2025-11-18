package ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.ssau.tk._repfor2lab_._OOP_.databaseDTO.MathFunctions;
import ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.Dao.JdbcMathFunctionRepository;
import ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.Dao.JdbcSimpleFunctionRepository;
import ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.Dao.JdbcUserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class jdbcMathFunctionsRepositoryTest {
    private JdbcMathFunctionRepository JdbcMathFunctionRepository;

    @BeforeEach
    void setUp() {
        JdbcMathFunctionRepository = new JdbcMathFunctionRepository();
        JdbcMathFunctionRepository.createTable();
    }

    @AfterEach
    void tearDown() {
        var f = new JdbcMathFunctionRepository();
        f.deleteAllFunctions();
        var u = new JdbcUserRepository();
        u.deleteAllUsers();
    }

    @Test
    void testFullCrudForMathFunctions() {
        // CREATE - Добавляем несколько функций
        JdbcMathFunctionRepository.createTable();

        //INSERT
        JdbcSimpleFunctionRepository s = new JdbcSimpleFunctionRepository();
        s.addSimpleFunction("SqrFunc", "Квадратичная функция");

        JdbcUserRepository u = new JdbcUserRepository();
        u.addUser("array", "login", "hardpassword", "user");
        int id = u.selectIdByLogin("login");

        JdbcMathFunctionRepository.addMathFunction("x^2-1", 100, -42.2,
                42.2, id, "SqrFunc");
        JdbcMathFunctionRepository.addMathFunction("x^3-1", 100, -42.2,
                42.2, id, "SqrFunc");
        JdbcMathFunctionRepository.addMathFunction("x^4-1", 100, -42.2,
                42.2, id, "SqrFunc");

        // READ - Получаем все функции
        List<MathFunctions> allFunctions = JdbcMathFunctionRepository.selectAllMathFunctionsSortedByUserLogins();
        assertFalse(allFunctions.isEmpty());

        List<MathFunctions> sinLocalName = JdbcMathFunctionRepository.selectMathFunctionsByUserId(id);
        assertNotNull(sinLocalName);

        // UPDATE - Обновляем локальное имя
        JdbcMathFunctionRepository.updateFunctionNameByFunctionId("dsdsd", 2);

        // DELETE - Удаляем одну функцию
        JdbcMathFunctionRepository.deleteMathFunctionByFunctionId(2);
        s.deleteAllFunctions();
    }
}