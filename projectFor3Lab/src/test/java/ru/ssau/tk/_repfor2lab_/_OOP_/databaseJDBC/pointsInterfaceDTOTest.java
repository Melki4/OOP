package ru.ssau.tk._repfor2lab_._OOP_.databaseJDBC;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.ssau.tk._repfor2lab_._OOP_.databaseDTO.PointDTO;
import ru.ssau.tk._repfor2lab_._OOP_.functions.ArrayTabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.MathFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.Point;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class pointsInterfaceDTOTest {
    private pointsInterface pointsInterface;
    private simpleFunctionInterface simpleFunctionInterface;

    @BeforeEach
    void setUp() {
        pointsInterface = new pointsInterface();
        simpleFunctionInterface = new simpleFunctionInterface();

        // таблица функций нужна, т.к. в points есть внешний ключ
        simpleFunctionInterface.createTable();
        pointsInterface.createTable();
    }

    @AfterEach
    void tearDown() {
        pointsInterface.deleteAllPoints();
        simpleFunctionInterface.deleteAllFunctions();
        var boof_to_clear = new mathFunctionsInterface();
        boof_to_clear.deleteAllFunctions();
    }

    @Test
    void selectAllPointsAsDTO() {
        MathFunction mathFunction = (double x)-> x*x + 2*x +1;
        var array = new ArrayTabulatedFunction(mathFunction, -100.0, 100.0, 1000);

        List<Point> points = new ArrayList<>();
        for (int i = 0; i< array.getCount(); ++i){
            Point p = new Point(array.getX(i), array.getY(i));
            points.add(p);
        }

        userInterface u = new userInterface();
        u.addUser("array", "login", "hardpassword", "user");
        int user_id = u.selectIdByLogin("login");

        mathFunctionsInterface m = new mathFunctionsInterface();
        m.addMathFunction("x^2+2x+1", 1000, -100.0,
                1, user_id, "SqrFunc");

        int function_id = Integer.parseInt(m.selectAllMathFunctions().get(0).split(" ")[0]);

        pointsInterface.bulkInsertPointsDirect(points, function_id);
        List<PointDTO> pointDTOS = pointsInterface.selectAllPointsAsDTO();
        List<PointDTO> pointDTOS1 = pointsInterface.selectPointsByFunctionIdAsDTO(function_id);

        for (int i =0; i< pointDTOS.size(); ++i){
            assertEquals(array.getX(i), pointDTOS.get(i).getXValue(), 0.000001);
            assertEquals(array.getY(i), pointDTOS.get(i).getYValue(), 0.000001);

            assertEquals(array.getX(i), pointDTOS1.get(i).getXValue(), 0.000001);
            assertEquals(array.getY(i), pointDTOS1.get(i).getYValue(), 0.000001);
        }

        System.out.println("Done");
    }
}