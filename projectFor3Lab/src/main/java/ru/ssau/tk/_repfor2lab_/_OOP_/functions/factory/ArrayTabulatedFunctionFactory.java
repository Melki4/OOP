package ru.ssau.tk._repfor2lab_._OOP_.functions.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ssau.tk._repfor2lab_._OOP_.functions.ArrayTabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.TabulatedFunction;

public class ArrayTabulatedFunctionFactory implements TabulatedFunctionFactory{
    public static final Logger LOGGER = LoggerFactory.getLogger(ArrayTabulatedFunctionFactory.class);
    @Override
    public TabulatedFunction create(double[] xValues, double[] yValues) {
        LOGGER.info("Создание массива из табулированных функций");
        return new ArrayTabulatedFunction(xValues, yValues);
    }

    public ArrayTabulatedFunction create(Double[] xValues, Double[] yValues) {
        LOGGER.info("Создание массива табулированных функций");

        double[] primitiveDoublesX = new double[xValues.length];
        double[] primitiveDoublesY = new double[yValues.length];

        for (int i = 0; i < xValues.length; i++) {
            primitiveDoublesX[i] = xValues[i]; // Автоматическое разворачивание
        }

        for (int i = 0; i < yValues.length; i++) {
            primitiveDoublesY[i] = yValues[i]; // Автоматическое разворачивание
        }

        return new ArrayTabulatedFunction(primitiveDoublesX, primitiveDoublesY);
    }
}
