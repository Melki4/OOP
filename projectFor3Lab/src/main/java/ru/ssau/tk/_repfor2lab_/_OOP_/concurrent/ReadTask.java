package ru.ssau.tk._repfor2lab_._OOP_.concurrent;

import ru.ssau.tk._repfor2lab_._OOP_.functions.TabulatedFunction;

public class ReadTask implements Runnable{
    private final TabulatedFunction function;
    public ReadTask(TabulatedFunction func) {
        this.function = func;
    }

    @Override
    public void run() {
        for (int i = 0; i < function.getCount(); i++) {
            synchronized (function) {
                double x = function.getX(i);
                double y = function.getY(i);
                System.out.printf("After read: i = %d, x = %f, y = %f%n", i, x, y);
            }
        }
    }
}
