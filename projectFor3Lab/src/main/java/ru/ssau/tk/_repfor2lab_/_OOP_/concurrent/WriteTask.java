package ru.ssau.tk._repfor2lab_._OOP_.concurrent;

import ru.ssau.tk._repfor2lab_._OOP_.functions.TabulatedFunction;

public class WriteTask implements Runnable{
    private final TabulatedFunction function;
    private final double value;
    public WriteTask(TabulatedFunction func, double value) {
        this.function = func;
        this.value = value;
    }

    @Override
    public void run() {
        for (int i = 0; i < function.getCount(); i++) {
            synchronized (function) {
                function.setY(i, value);
                System.out.printf("Writing for index %d complete%n", i);
            }
        }
    }
}
