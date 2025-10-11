package ru.ssau.tk._repfor2lab_._OOP_.operations;

import ru.ssau.tk._repfor2lab_._OOP_.functions.MathFunction;

public interface DifferentialOperator<T extends MathFunction> {
    T derive(T function);
}
