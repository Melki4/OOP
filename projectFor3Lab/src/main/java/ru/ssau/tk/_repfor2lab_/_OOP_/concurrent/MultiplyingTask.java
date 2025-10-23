package ru.ssau.tk._repfor2lab_._OOP_.concurrent;

import ru.ssau.tk._repfor2lab_._OOP_.functions.TabulatedFunction;

public class MultiplyingTask implements Runnable{

    public boolean done = false;

    TabulatedFunction function;

    @Override
    public void run() {

        int h = function.getCount();

        for(int i=0; i<h;++i){
            synchronized (function){
                double boof = function.getY(i);
                function.setY(i, boof*2);
            }
        }

        System.out.println("Текущий поток " + Thread.currentThread().getName() + " закончил выполнение задачи");

        done = true;
    }

    public MultiplyingTask(TabulatedFunction f){
        function=f;
    }
}
