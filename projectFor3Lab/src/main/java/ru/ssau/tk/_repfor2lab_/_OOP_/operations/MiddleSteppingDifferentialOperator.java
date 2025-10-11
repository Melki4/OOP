package ru.ssau.tk._repfor2lab_._OOP_.operations;

import ru.ssau.tk._repfor2lab_._OOP_.functions.MathFunction;

public class MiddleSteppingDifferentialOperator extends SteppingDifferentialOperator{

    public MiddleSteppingDifferentialOperator(double step) {
        super(step);
    }

    @Override
    public MathFunction derive(MathFunction function) {
        return new MathFunction() {
            @Override
            public double apply(double x) {
                double f_x_plus_h = function.apply(x+step);
                double f_x_minus_h = function.apply(x-step);

                return (f_x_plus_h-f_x_minus_h)/(2*step);
            }
        };
    }
}
