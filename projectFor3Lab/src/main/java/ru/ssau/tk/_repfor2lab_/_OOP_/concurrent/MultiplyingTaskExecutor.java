package ru.ssau.tk._repfor2lab_._OOP_.concurrent;

import ru.ssau.tk._repfor2lab_._OOP_.functions.LinkedListTabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.MathFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.UnitFunction;

import java.util.ArrayList;

public class MultiplyingTaskExecutor {

    public static void main(String[] s) throws InterruptedException {
        MathFunction f1 = new UnitFunction();

        LinkedListTabulatedFunction f = new LinkedListTabulatedFunction(f1, 1.0, 1000, 10000);

        var list = new ArrayList<Thread>();

        for (int i=0; i<10;++i){
            MultiplyingTask task = new MultiplyingTask(f);
            String name = "Thread" + String.valueOf(i);
            Thread thread = new Thread(task, name);
            list.add(thread);
        }

        for (int i=0; i<10;++i) list.get(i).start();
        Thread.sleep(3000);

        System.out.println(f.getY(0));
    }
}
