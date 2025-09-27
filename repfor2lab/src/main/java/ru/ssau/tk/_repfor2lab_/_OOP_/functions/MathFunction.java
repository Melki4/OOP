package ru.ssau.tk._repfor2lab_._OOP_.functions;

interface MathFunction {

    double apply(double x);

    default CompositeFunction andThen (MathFunction afterFunction){
        return new CompositeFunction(afterFunction, this);//ко второй применяет первую т.е. к this применит afterFunction
    }
}
