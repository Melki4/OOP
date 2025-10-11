package ru.ssau.tk._repfor2lab_._OOP_.operations;

import ru.ssau.tk._repfor2lab_._OOP_.functions.Point;
import ru.ssau.tk._repfor2lab_._OOP_.functions.TabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.factory.ArrayTabulatedFunctionFactory;
import ru.ssau.tk._repfor2lab_._OOP_.functions.factory.TabulatedFunctionFactory;

public class TabulatedDifferentialOperator implements DifferentialOperator<TabulatedFunction> {
    private TabulatedFunctionFactory factory;

    public TabulatedDifferentialOperator(TabulatedFunctionFactory factory){
        this.factory = factory;
    }
    public TabulatedDifferentialOperator() {
        this.factory = new ArrayTabulatedFunctionFactory();
    }
    public TabulatedFunctionFactory getFactory(){
        return factory;
    }
    public void setFactory(TabulatedFunctionFactory factory) {
        this.factory = factory;
    }

    @Override
    public TabulatedFunction derive(TabulatedFunction func) {
        Point[] points = TabulatedFunctionOperationService.asPoints(func);
        int count = points.length;
        double[] xValues = new double[count];
        double[] yValues = new double[count];
        for (int i = 0; i < count; i++){
            xValues[i] = points[i].x;
        }
        for (int i = 0; i < count -1; i++) {
            double derivativeX = points[i+1].x - points[i].x; //правая производная
            double derivativeY = points[i+1].y - points[i].y;
            yValues[i] = derivativeY / derivativeX;
        }
        yValues[count - 1] = yValues[count - 2];
        return factory.create(xValues, yValues);
    }
}
