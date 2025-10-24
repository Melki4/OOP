package ru.ssau.tk._repfor2lab_._OOP_.operations;

import ru.ssau.tk._repfor2lab_._OOP_.functions.MathFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.TabulatedFunction;

public interface IntegrationOperatop<T extends MathFunction>  {
    T integrate(T function);
}
