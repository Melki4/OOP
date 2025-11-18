package ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.ssau.tk._repfor2lab_._OOP_.databaseDTO.Points;
import ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.Dao.JdbcMathFunctionRepository;
import ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.Dao.JdbcPointRepository;
import ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.Dao.JdbcSimpleFunctionRepository;
import ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.Dao.JdbcUserRepository;
import ru.ssau.tk._repfor2lab_._OOP_.functions.ArrayTabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.MathFunction;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class jdbcPointRepositoryDTOTest {
    private JdbcPointRepository JdbcPointRepository;
    private JdbcSimpleFunctionRepository JdbcSimpleFunctionRepository;

    @BeforeEach
    void setUp() {
        JdbcPointRepository = new JdbcPointRepository();
        JdbcSimpleFunctionRepository = new JdbcSimpleFunctionRepository();

        // таблица функций нужна, т.к. в points есть внешний ключ
        JdbcSimpleFunctionRepository.createTable();
        JdbcPointRepository.createTable();
    }

    @AfterEach
    void tearDown() {
        JdbcPointRepository.deleteAllPoints();
        JdbcSimpleFunctionRepository.deleteAllFunctions();
        var boof_to_clear = new JdbcMathFunctionRepository();
        boof_to_clear.deleteAllFunctions();
    }

    @Test
    void selectAllPointsAsDTO() {
        MathFunction mathFunction = (double x)-> x*x + 2*x +1;
        var array = new ArrayTabulatedFunction(mathFunction, -100.0, 100.0, 1000);

        List<ru.ssau.tk._repfor2lab_._OOP_.functions.Point> points = new ArrayList<>();
        for (int i = 0; i< array.getCount(); ++i){
            ru.ssau.tk._repfor2lab_._OOP_.functions.Point p = new ru.ssau.tk._repfor2lab_._OOP_.functions.Point(array.getX(i), array.getY(i));
            points.add(p);
        }

        JdbcUserRepository u = new JdbcUserRepository();
        u.addUser("array", "login", "hardpassword", "user");
        int user_id = u.selectIdByLogin("login");

        JdbcMathFunctionRepository m = new JdbcMathFunctionRepository();
        m.addMathFunction("x^2+2x+1", 1000, -100.0,
                1, user_id, "SqrFunc");

        int function_id = m.selectMathFunctionsByName("x^2+2x+1").getFunctionId().intValue();

        JdbcPointRepository.bulkInsertPointsDirect(points, function_id);
        List<Points> pointsDTOS = JdbcPointRepository.selectAllPoints();
        List<Points> pointsDTOS1 = JdbcPointRepository.selectPointsByFunctionId(function_id);

        for (int i = 0; i< pointsDTOS.size(); ++i){
            assertEquals(array.getX(i), pointsDTOS.get(i).getXValue(), 0.000001);
            assertEquals(array.getY(i), pointsDTOS.get(i).getYValue(), 0.000001);

            assertEquals(array.getX(i), pointsDTOS1.get(i).getXValue(), 0.000001);
            assertEquals(array.getY(i), pointsDTOS1.get(i).getYValue(), 0.000001);
        }

        System.out.println("Done");
    }
}