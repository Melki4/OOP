package ru.ssau.tk._repfor2lab_._OOP_.functions.factory;
import ru.ssau.tk._repfor2lab_._OOP_.functions.TabulatedFunction;

public interface TabulatedFunctionFactory {
    TabulatedFunction create(double[] xValues, double[] yValues);
}
