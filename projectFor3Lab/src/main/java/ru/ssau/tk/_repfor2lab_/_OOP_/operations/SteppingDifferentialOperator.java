package ru.ssau.tk._repfor2lab_._OOP_.operations;

import ru.ssau.tk._repfor2lab_._OOP_.functions.MathFunction;

public abstract class SteppingDifferentialOperator implements DifferentialOperator<MathFunction>{

    protected double step;

    public SteppingDifferentialOperator(double step){
        if(step<=0 || Double.isInfinite(step) || Double.isNaN(step)) throw new IllegalArgumentException("Неверный аргумент в диф. операторе");
        this.step = step;
    }

    public double getStep(){
        return step;
    }

    public void setStep(double step){
        if(step<=0 || Double.isInfinite(step) || Double.isNaN(step)) throw new IllegalArgumentException("Неверный аргумент в диф. операторе");
        this.step = step;
    }
}
