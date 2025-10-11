package ru.ssau.tk._repfor2lab_._OOP_.operations;

import ru.ssau.tk._repfor2lab_._OOP_.exceptions.InconsistentFunctionsException;
import ru.ssau.tk._repfor2lab_._OOP_.functions.factory.ArrayTabulatedFunctionFactory;
import ru.ssau.tk._repfor2lab_._OOP_.functions.factory.TabulatedFunctionFactory;
import ru.ssau.tk._repfor2lab_._OOP_.functions.*;

public class TabulatedFunctionOperationService {

    TabulatedFunctionFactory factory;

    TabulatedFunctionOperationService(TabulatedFunctionFactory factory){
        this.factory = factory;
    }

    TabulatedFunctionOperationService(){
        factory = new ArrayTabulatedFunctionFactory();
    }

    TabulatedFunctionFactory get() {
        return factory;
    }

    void set(TabulatedFunctionFactory factory){
        this.factory = factory;
    }

    public static Point[] asPoints(TabulatedFunction tabulatedFunction){
        Point[] toReturn = new Point[tabulatedFunction.getCount()];
        int i=0;
        for (var point : tabulatedFunction) {
            toReturn[i++] = point;
        }
        return toReturn;
    }

    private interface BiOperation {
        double apply(double u, double v);
    }

    private TabulatedFunction doOperation(TabulatedFunction a, TabulatedFunction b, BiOperation operation){
        if (a.getCount()!=b.getCount()) throw new InconsistentFunctionsException("Разное кол-во элементов в функциях");

        Point[] aArray = asPoints(a);
        Point[] bArray = asPoints(b);

        double[] xValues = new double[aArray.length];
        double[] yValues = new double[aArray.length];

        for (int i=0; i< aArray.length; ++i){
            if (aArray[i].x!=bArray[i].x) throw new InconsistentFunctionsException("Разные элементы икс в массивах");
            xValues[i] = aArray[i].x;
            yValues[i] = operation.apply(aArray[i].y, bArray[i].y);
        }
        return factory.create(xValues, yValues);
    }

    public TabulatedFunction addition(TabulatedFunction a, TabulatedFunction b){
        return doOperation(a, b, (double f, double s) -> f + s);
    }

    public TabulatedFunction subtraction(TabulatedFunction a, TabulatedFunction b){
        return doOperation(a, b, (double f, double s) -> f - s);
    }
    public TabulatedFunction multiplication(TabulatedFunction a, TabulatedFunction b) {
        return doOperation(a, b, (double f, double s) -> f * s);
    }
    public TabulatedFunction division(TabulatedFunction a, TabulatedFunction b) {
        return doOperation(a, b, (double f, double s) -> f / s);
    }

}
