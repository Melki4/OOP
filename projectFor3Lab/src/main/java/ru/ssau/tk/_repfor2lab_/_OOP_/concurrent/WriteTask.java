package ru.ssau.tk._repfor2lab_._OOP_.concurrent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ssau.tk._repfor2lab_._OOP_.functions.TabulatedFunction;

public class WriteTask implements Runnable{
    private final TabulatedFunction function;
    private final double value;
    private static final Logger LOGGER = LoggerFactory.getLogger(WriteTask.class);

    public WriteTask(TabulatedFunction func, double value) {
        LOGGER.info("Создание WriteTask");
        this.function = func;
        this.value = value;
    }

    @Override
    public void run() {
        LOGGER.trace("записываем в функцию");
        for (int i = 0; i < function.getCount(); i++) {
            synchronized (function) {
                function.setY(i, value);
                System.out.printf("Writing for index %d complete%n", i);
            }
        }
        LOGGER.warn("закончили запись");
    }
}
