package ru.ssau.tk._repfor2lab_._OOP_.operations;

import ru.ssau.tk._repfor2lab_._OOP_.concurrent.SynchronizedTabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.MathFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.Point;
import ru.ssau.tk._repfor2lab_._OOP_.functions.TabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.factory.ArrayTabulatedFunctionFactory;
import ru.ssau.tk._repfor2lab_._OOP_.functions.factory.TabulatedFunctionFactory;

public class TabulatedSimpsonMethod implements IntegrationOperatop<TabulatedFunction>{

    private TabulatedFunctionFactory factory;

    public TabulatedSimpsonMethod(TabulatedFunctionFactory factory){
        this.factory = factory;
    }

    public TabulatedSimpsonMethod() {
        this.factory = new ArrayTabulatedFunctionFactory();
    }

    public TabulatedFunctionFactory getFactory(){
        return factory;
    }

    public void setFactory(TabulatedFunctionFactory factory) {
        this.factory = factory;
    }

    public TabulatedFunction integrate(TabulatedFunction function) {
        Point[] points = TabulatedFunctionOperationService.asPoints(function);

        int count = points.length;

        double[] xValues = new double[count];
        double[] yValues = new double[count];

        for (int i = 0; i < count; i++){
            xValues[i] = points[i].x;
        }

        double Sum = function.apply(xValues[0]);
        double step = (xValues[xValues.length-1]-xValues[0])/xValues.length;
        double x = xValues[0];

        double OddSum = 0.0;
        double EvenSum = 0.0;

        for (int i =0; i< xValues.length; ++i){
            double functionValue = function.apply(x);

            if (i == 0) {
                Sum += functionValue;
            } else {
                if (i % 2 == 0) {
                    EvenSum += functionValue;
                } else {
                    OddSum += functionValue;
                }
            }
            x += step;
            yValues[i] = (step / 3) * (Sum + 2 * EvenSum + 4 * OddSum);
        }

        return factory.create(xValues, yValues);
    }

    public TabulatedFunction integrateSynchronously(TabulatedFunction func){

        SynchronizedTabulatedFunction f;

        if (func instanceof SynchronizedTabulatedFunction) f = (SynchronizedTabulatedFunction) func;

        else f = new SynchronizedTabulatedFunction(func);

        return f.doSynchronously(this::integrate);
    }
}
