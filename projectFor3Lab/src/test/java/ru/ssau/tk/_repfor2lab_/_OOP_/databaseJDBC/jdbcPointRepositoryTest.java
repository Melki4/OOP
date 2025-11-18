package ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC;

import org.junit.jupiter.api.*;
import ru.ssau.tk._repfor2lab_._OOP_.databaseDTO.Points;
import ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.Dao.JdbcMathFunctionRepository;
import ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.Dao.JdbcPointRepository;
import ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.Dao.JdbcSimpleFunctionRepository;
import ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.Dao.JdbcUserRepository;
import ru.ssau.tk._repfor2lab_._OOP_.exceptions.DataDoesNotExistException;
import ru.ssau.tk._repfor2lab_._OOP_.functions.ArrayTabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.MathFunction;

import ru.ssau.tk._repfor2lab_._OOP_.functions.Point;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class jdbcPointRepositoryTest {

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
    @DisplayName("Добавление, выборка и массовое удаление точек")
    void testMassInsertAndDelete() {

        JdbcMathFunctionRepository s = new JdbcMathFunctionRepository();
        JdbcUserRepository u = new JdbcUserRepository();

        int user_id = u.selectIdByLogin("login");

        s.addMathFunction("x^2-1", 100, -42.2,
                42.2, user_id, "SqrFunc");

        int function_id = s.selectMathFunctionsByName("x^2-1").getFunctionId().intValue();

        JdbcSimpleFunctionRepository.addSimpleFunction("F", "Функция");

        // Массовое добавление
        for (int i = 0; i < 10; i++) {
            JdbcPointRepository.addPoint((double) i, (double) (i * 2), function_id);
        }

        List<Points> all = JdbcPointRepository.selectAllPoints();
        assertEquals(10, all.size());

        // deleteAllPoints
        JdbcPointRepository.deleteAllPoints();

        assertThrows(DataDoesNotExistException.class, ()->{JdbcPointRepository.selectAllPoints();});
    }

    @Test
    @DisplayName("Изменение X и Y по ID точки")
    void testUpdateOperations() {

        JdbcMathFunctionRepository ss = new JdbcMathFunctionRepository();
        JdbcUserRepository u = new JdbcUserRepository();

        int user_id = u.selectIdByLogin("login");

        ss.addMathFunction("x^2-1", 100, -42.2,
                42.2, user_id, "SqrFunc");

        int function_id = ss.selectMathFunctionsByName("x^2-1").getFunctionId().intValue();

        JdbcSimpleFunctionRepository.addSimpleFunction("F2", "Функция 2");

        JdbcPointRepository.addPoint(7.7, 8.8, function_id);

        List<Points> before = JdbcPointRepository.selectAllPoints();
        assertEquals(1, before.size());

        int id = before.get(0).getPointId().intValue();

        JdbcPointRepository.updateXValueById(100.0, id);
        JdbcPointRepository.updateYValueById(200.0, id);

        Points parts = JdbcPointRepository.selectPointByPointId(id);
        assertEquals(100.0, parts.getXValue());
        assertEquals(200.0, parts.getYValue());
    }

    @Test
    void testWithATF(){
        MathFunction mathFunction = (double x)-> x*x + 2*x +1;
        var array = new ArrayTabulatedFunction(mathFunction, -100.0, 100.0, 100000);

        List<Point> points = new ArrayList<>();

        for (int i = 0; i< array.getCount(); ++i){
            Point p = new Point(array.getX(i), array.getY(i));
            points.add(p);
        }

        JdbcUserRepository u = new JdbcUserRepository();
        u.addUser("array", "login", "hardpassword", "user");
        int user_id = u.selectIdByLogin("login");

        JdbcMathFunctionRepository m = new JdbcMathFunctionRepository();
        m.addMathFunction("x^2+2x+1", 100, -100.0,
                1, user_id, "SqrFunc");
        int function_id =  m.selectMathFunctionsByName("x^2+2x+1").getFunctionId().intValue();

        JdbcPointRepository.bulkInsertPointsDirect(points, function_id);

        System.out.println("Done");
    }
}
