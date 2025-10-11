package ru.ssau.tk._repfor2lab_._OOP_.operations;

import org.junit.jupiter.api.Test;
import ru.ssau.tk._repfor2lab_._OOP_.functions.ArrayTabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.Point;
import ru.ssau.tk._repfor2lab_._OOP_.functions.TabulatedFunction;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

class TabulatedFunctionOperationServiceTest {
    @Test
    void asPoints1() {
        double[] xValues1 = {1.0, 2.0, 3.0};
        double[] yValues1 = {1.0, 4.0, 9.0};
        TabulatedFunction arrayFunc = new ArrayTabulatedFunction(xValues1, yValues1);
        Point[] massive = TabulatedFunctionOperationService.asPoints(arrayFunc);
        for (int i=0; i< xValues1.length; ++i) {
            assertEquals(xValues1[i], massive[i].x);
            assertEquals(yValues1[i], massive[i].y);
        }
    }

    @Test
    void asPoints2() {
        double[] xValues1 = {1.0, 2.0, 3.0, 4.5, 5.5};
        double[] yValues1 = {1.0, 4.0, 9.05, 35.5, 5.5};

        TabulatedFunction arrayFunc = new ArrayTabulatedFunction(xValues1, yValues1);
        Point[] massive = TabulatedFunctionOperationService.asPoints(arrayFunc);

        Iterator<Point> iterator= arrayFunc.iterator();
        int i=0;
        while (iterator.hasNext()) {
            Point boof = iterator.next();
            assertEquals(boof.x, massive[i].x);
            assertEquals(boof.y, massive[i].y);
            ++i;
        }
    }
}