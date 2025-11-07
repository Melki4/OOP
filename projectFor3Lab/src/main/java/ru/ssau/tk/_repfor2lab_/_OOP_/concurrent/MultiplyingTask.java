package ru.ssau.tk._repfor2lab_._OOP_.concurrent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ssau.tk._repfor2lab_._OOP_.functions.TabulatedFunction;

public class MultiplyingTask implements Runnable{

    public boolean done = false;

    private final TabulatedFunction function;
    private static final Logger LOGGER = LoggerFactory.getLogger(MultiplyingTask.class);

    @Override
    public void run() {
        LOGGER.info("Starting multithread work");
        int h = function.getCount();

        for(int i=0; i<h;++i){
            synchronized (function){
                double boof = function.getY(i);
                function.setY(i, boof*2);
            }
        }

        System.out.println("Текущий поток " + Thread.currentThread().getName() + " закончил выполнение задачи");

        done = true;
        LOGGER.info("Work is done!");
    }

    public MultiplyingTask(TabulatedFunction f){
        function=f;
    }
}
