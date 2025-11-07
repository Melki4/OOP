package ru.ssau.tk._repfor2lab_._OOP_.concurrent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ssau.tk._repfor2lab_._OOP_.functions.TabulatedFunction;

public class ReadTask implements Runnable{
    private final TabulatedFunction function;
    private static final Logger LOGGER = LoggerFactory.getLogger(ReadTask.class);

    public ReadTask(TabulatedFunction func) {
        this.function = func;
    }

    @Override
    public void run() {
        LOGGER.trace("Начинаем считывать в readTask");
        for (int i = 0; i < function.getCount(); i++) {
            synchronized (function) {
                double x = function.getX(i);
                double y = function.getY(i);
                System.out.printf("After read: i = %d, x = %f, y = %f%n", i, x, y);
            }
        }
        LOGGER.trace("Закончили читать");
    }
}
