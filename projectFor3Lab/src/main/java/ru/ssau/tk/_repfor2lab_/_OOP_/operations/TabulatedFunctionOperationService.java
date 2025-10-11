package ru.ssau.tk._repfor2lab_._OOP_.operations;

import ru.ssau.tk._repfor2lab_._OOP_.functions.*;

public class TabulatedFunctionOperationService {
    public static Point[] asPoints(TabulatedFunction tabulatedFunction){
        Point[] toReturn = new Point[tabulatedFunction.getCount()];
        int i=0;
        for (var point : tabulatedFunction) {
           toReturn[i++] = point;
        }
        return toReturn;
    }
}
