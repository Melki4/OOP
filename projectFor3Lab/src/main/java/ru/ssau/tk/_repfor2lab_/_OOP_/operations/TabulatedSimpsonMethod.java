package ru.ssau.tk._repfor2lab_._OOP_.operations;

import ru.ssau.tk._repfor2lab_._OOP_.concurrent.SynchronizedTabulatedFunction;
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

        // Если количество точек четное, уменьшаем на 1
        if (count % 2 == 0) {
            count--;
        }

        double[] xValues = new double[count];
        double[] yValues = new double[count];

        for (int i = 0; i < count; i++){
            xValues[i] = points[i].x;
        }

        double sum = yValues[0] = points[0].y + function.apply(points[count-1].x); // f(x0)
        double step = (xValues[xValues.length-1]-xValues[0])/xValues.length;

        double OddSum = 0.0;
        double EvenSum = 0.0;

        // Пропускаем первую и последнюю точки
        for (int i = 1; i < count - 1; i++) {
            double functionValue = function.apply(points[i].x);
            if (i % 2 == 1) {
                OddSum += functionValue;  // нечетные индексы: 1, 3, 5...
            } else {
                EvenSum += functionValue; // четные индексы: 2, 4, 6...
            }
            yValues[i] = (step / 3) * (4 * OddSum + 2 * EvenSum + sum);
            System.out.println(yValues[i]);
        }

        yValues[count-1] = (step / 3) * (sum + 4 * OddSum + 2 * EvenSum);

        return factory.create(xValues, yValues);
    }

    public TabulatedFunction integrateSynchronously(TabulatedFunction func){

        SynchronizedTabulatedFunction f;

        if (func instanceof SynchronizedTabulatedFunction) f = (SynchronizedTabulatedFunction) func;

        else f = new SynchronizedTabulatedFunction(func);

        return f.doSynchronously(this::integrate);
    }
}
