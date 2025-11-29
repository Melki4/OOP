package ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC.Dao;

import org.junit.jupiter.api.*;
import ru.ssau.tk._repfor2lab_._OOP_.databaseEnteties.Users;
import ru.ssau.tk._repfor2lab_._OOP_.functions.ArrayTabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.MathFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.Point;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class JdbcPointRepositoryTest {

    static List<Users> array = new ArrayList<>();

    @BeforeAll
    static void arrayCreation(){

        JdbcUserRepository userRepository = new JdbcUserRepository();
        userRepository.createTable();
        userRepository.createUser("array", "vovapain", "vivatVOVA", "user");
        userRepository.createUser("list", "yojo", "vivatyojo", "admin");

        JdbcSimpleFunctionRepository simpleFunctionRepository= new JdbcSimpleFunctionRepository();
        simpleFunctionRepository.createTable();
        simpleFunctionRepository.createSimpleFunction("Квадратичная функция");
        simpleFunctionRepository.createSimpleFunction("Табулированная функция");

        JdbcMathFunctionRepository mathFunctionRepository1 = new JdbcMathFunctionRepository();
        mathFunctionRepository1.createTable();
        mathFunctionRepository1.createMathFunction("x^2", 100, -1,
                13, userRepository.selectIdByLogin("yojo"), "Квадратичная функция");
        mathFunctionRepository1.createMathFunction("x^2", 1000, -1,
                13, userRepository.selectIdByLogin("yojo"), "Квадратичная функция");
        mathFunctionRepository1.createMathFunction("e^(x^2-1)", 100, -1,
                123, userRepository.selectIdByLogin("vovapain"), "Табулированная функция");

        array = userRepository.findAllUsers();

        JdbcPointRepository pointRepository = new JdbcPointRepository();
        pointRepository.createTable();

        MathFunction mathFunction = (double x)-> x*x + 2*x +1;
        var array = new ArrayTabulatedFunction(mathFunction, -100.0, 100.0, 10);

        List<Point> points = new ArrayList<>();
        for (int i = 0; i< array.getCount(); ++i){
            Point p = new Point(array.getX(i), array.getY(i));
            points.add(p);
        }

        mathFunctionRepository1.createMathFunction("Function", 10, -100.0,
                100.0, userRepository.selectIdByLogin("yojo"), "Табулированная функция");

        pointRepository.addManyPoints(points, mathFunctionRepository1.findMathFunctionIdComplex(-100.0,
                100.0, 10, "Function"));
    }

    @AfterAll
    static void cleaning(){
        JdbcUserRepository userRepository = new JdbcUserRepository();
        userRepository.deleteAllUsers();
        JdbcMathFunctionRepository mathFunctionRepository = new JdbcMathFunctionRepository();
        mathFunctionRepository.deleteAllFunctions();
        JdbcSimpleFunctionRepository simpleFunctionRepository = new JdbcSimpleFunctionRepository();
        simpleFunctionRepository.deleteAllFunctions();
        JdbcPointRepository pointRepository = new JdbcPointRepository();
        pointRepository.deleteAllPoints();
    }

    @Order(1)
    @Test
    void findPointsByFunctionId() {
        JdbcMathFunctionRepository mathFunctionRepository = new JdbcMathFunctionRepository();

        JdbcPointRepository pointRepository = new JdbcPointRepository();
        assertEquals(10, pointRepository.findPointsByFunctionId(mathFunctionRepository.findMathFunctionIdComplex(-100.0,
                100.0, 10, "Function")).size());
    }

    @Order(2)
    @Test
    void findPointsByFunctionIdSorted() {
        JdbcMathFunctionRepository mathFunctionRepository = new JdbcMathFunctionRepository();

        JdbcPointRepository pointRepository = new JdbcPointRepository();
        assertEquals(10, pointRepository.findPointsByFunctionIdSorted(mathFunctionRepository.findMathFunctionIdComplex(-100.0,
                100.0, 10, "Function")).size());
    }

    @Order(3)
    @Test
    void updateXValueByFunctionIdAndOldX() {
        JdbcMathFunctionRepository mathFunctionRepository = new JdbcMathFunctionRepository();

        JdbcPointRepository pointRepository = new JdbcPointRepository();
        pointRepository.updateXValueByFunctionIdAndOldX(-100.0, mathFunctionRepository.findMathFunctionIdComplex(-100.0,
                100.0, 10, "Function"), -90.0);

    }

    @Order(4)
    @Test
    void updateYValueByFunctionIdAndOldY() {
        JdbcMathFunctionRepository mathFunctionRepository = new JdbcMathFunctionRepository();

        JdbcPointRepository pointRepository = new JdbcPointRepository();
        pointRepository.updateYValueByFunctionIdAndOldY(9801.0, mathFunctionRepository.findMathFunctionIdComplex(-100.0,
                100.0, 10, "Function"), 9000.0);
    }

    @Order(5)
    @Test
    void createPoint() {
        JdbcMathFunctionRepository mathFunctionRepository = new JdbcMathFunctionRepository();

        JdbcPointRepository pointRepository = new JdbcPointRepository();
        pointRepository.createPoint(99999., 99999., mathFunctionRepository.findMathFunctionIdComplex(-100.0,
                100.0, 10, "Function") );
    }

    @Order(6)
    @Test
    void deletePointsByFunctionId() {
        JdbcMathFunctionRepository mathFunctionRepository = new JdbcMathFunctionRepository();

        JdbcPointRepository pointRepository = new JdbcPointRepository();
        pointRepository.deletePointsByFunctionId(mathFunctionRepository.findMathFunctionIdComplex(-100.0,
                100.0, 10, "Function"));
    }
}