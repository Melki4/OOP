package ru.ssau.tk._repfor2lab_._OOP_.functions;

public class CompositeFunction implements MathFunction {

    private  MathFunction firstFunction, secondFunction;

    public CompositeFunction(MathFunction firstFunction, MathFunction secondFunction) {
        this.firstFunction = firstFunction;
        this.secondFunction = secondFunction;
    }

    public double apply(double x){
        return secondFunction.apply(firstFunction.apply(x));
    }
}
