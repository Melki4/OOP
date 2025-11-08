package ru.ssau.tk._repfor2lab_._OOP_.functions.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ssau.tk._repfor2lab_._OOP_.functions.LinkedListTabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.TabulatedFunction;

public class LinkedListTabulatedFunctionFactory implements TabulatedFunctionFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(LinkedListTabulatedFunctionFactory.class);
    @Override
    public TabulatedFunction create(double[] xValues, double[] yValues) {
        LOGGER.info("Создание списка табулированных функций");
        return new LinkedListTabulatedFunction(xValues, yValues);
    }
}
