package ru.ssau.tk._repfor2lab_._OOP_.functions;

public class ConstantFunction implements MathFunction{

    private final double CONSTANT;

    public ConstantFunction(double arg){
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
