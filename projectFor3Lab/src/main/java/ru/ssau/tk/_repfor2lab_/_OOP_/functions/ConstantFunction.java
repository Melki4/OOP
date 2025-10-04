package ru.ssau.tk._repfor2lab_._OOP_.functions;

public class ConstantFunction implements MathFunction{//всегда возвращает какую-то константу

    private final double CONSTANT;

    public ConstantFunction(double arg){//задать константу
        CONSTANT = arg;
    }

    public double get_constant(){
        return CONSTANT;
    }

    @Override
    public double apply(double x) {
        return CONSTANT;
    }
}
