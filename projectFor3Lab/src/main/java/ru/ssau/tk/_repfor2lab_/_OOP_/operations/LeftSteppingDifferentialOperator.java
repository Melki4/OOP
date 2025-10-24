package ru.ssau.tk._repfor2lab_._OOP_.operations;

import ru.ssau.tk._repfor2lab_._OOP_.concurrent.SynchronizedTabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.MathFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.TabulatedFunction;

public class LeftSteppingDifferentialOperator extends SteppingDifferentialOperator{

    public LeftSteppingDifferentialOperator(double step) {
        super(step);
    }

    @Override
    public MathFunction derive(MathFunction function) {
        return new MathFunction() {
            @Override
            public double apply(double x) {
                double f_x = function.apply(x);
                double f_x_minus_h = function.apply(x-step);

                return (f_x-f_x_minus_h)/step;
            }
        };
    }

    public MathFunction deriveSynchronously(TabulatedFunction func){

        SynchronizedTabulatedFunction f;

        if (func instanceof SynchronizedTabulatedFunction) f = (SynchronizedTabulatedFunction) func;

        else f = new SynchronizedTabulatedFunction(func);

        return f.doSynchronously(this::derive);
    }
}
