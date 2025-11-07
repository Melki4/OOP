package ru.ssau.tk._repfor2lab_._OOP_.operations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ssau.tk._repfor2lab_._OOP_.concurrent.SynchronizedTabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.MathFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.TabulatedFunction;

public class MiddleSteppingDifferentialOperator extends SteppingDifferentialOperator{

    private static final Logger LOGGER = LoggerFactory.getLogger(MiddleSteppingDifferentialOperator.class);

    public MiddleSteppingDifferentialOperator(double step) {
        super(step);
    }

    @Override
    public MathFunction derive(MathFunction function) {
        LOGGER.trace("начало вычисления центральной производной для табулированной функции");
        return new MathFunction() {
            @Override
            public double apply(double x) {
                double f_x_plus_h = function.apply(x+step);
                double f_x_minus_h = function.apply(x-step);
                LOGGER.trace("конец вычисления центральной производной для табулированной функции");
                return (f_x_plus_h-f_x_minus_h)/(2*step);
            }
        };
    }

    public MathFunction deriveSynchronously(TabulatedFunction func){
        LOGGER.trace("начало синхронного вычисления центральной производной для табулированной функции");
        SynchronizedTabulatedFunction f;

        if (func instanceof SynchronizedTabulatedFunction) f = (SynchronizedTabulatedFunction) func;

        else f = new SynchronizedTabulatedFunction(func);
        LOGGER.trace("конец синхронного вычисления центральной производной для табулированной функции");
        return f.doSynchronously(this::derive);
    }
}
