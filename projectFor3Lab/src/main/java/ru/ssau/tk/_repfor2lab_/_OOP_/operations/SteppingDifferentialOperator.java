package ru.ssau.tk._repfor2lab_._OOP_.operations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ssau.tk._repfor2lab_._OOP_.functions.LinkedListTabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.MathFunction;

public abstract class SteppingDifferentialOperator implements DifferentialOperator<MathFunction>{

    protected double step;
    private static final Logger LOGGER = LoggerFactory.getLogger(SteppingDifferentialOperator.class);

    public SteppingDifferentialOperator(double step){
        if(step<=0 || Double.isInfinite(step) || Double.isNaN(step)) {
            LOGGER.warn("Неверный аргумент в диф. операторе передан был в конструктор");
            throw new IllegalArgumentException("Неверный аргумент в диф. операторе");
        }
        this.step = step;
    }

    public double getStep(){
        return step;
    }

    public void setStep(double step){
        if(step<=0 || Double.isInfinite(step) || Double.isNaN(step)) {
            LOGGER.warn("Неверный аргумент в диф. операторе setStep");
            throw new IllegalArgumentException("Неверный аргумент в диф. операторе");
        }
        this.step = step;
    }
}
