package ru.ssau.tk._repfor2lab_._OOP_.concurrent;

import ru.ssau.tk._repfor2lab_._OOP_.functions.TabulatedFunction;

public class MultiplyingTask implements Runnable{

    TabulatedFunction function;

    @Override
    public void run() {

        double boof;

        for(int i=0; i<function.getCount();++i){
            boof = function.getY(i);
            function.setY(i, boof*2);
        }

        System.out.println("Текущий поток " + Thread.currentThread().getName() + " закончил выполнение задачи");
    }

    public MultiplyingTask(TabulatedFunction f){
        function=f;
    }
}
