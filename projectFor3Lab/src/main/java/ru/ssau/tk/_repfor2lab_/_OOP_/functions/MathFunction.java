package ru.ssau.tk._repfor2lab_._OOP_.functions;

interface MathFunction {//автоматически публичный и абстрактный, модификаторы будут лишними

    double apply(double x);

    default CompositeFunction andThen (MathFunction afterFunction){
        //примет d(x), наша ф-ция f(x) - this
        //выполнит f(d(x))
        return new CompositeFunction(afterFunction, this);//ко второй применяет первую т.е.
        // к this применит afterFunction : особенности реализации сложной функции
    }
}
