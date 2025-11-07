package ru.ssau.tk._repfor2lab_._OOP_.integration;

import ru.ssau.tk._repfor2lab_._OOP_.functions.ArrayTabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.MathFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.TabulatedFunction;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

public class Test {

    private static final int amountOfThreads = 8;
    private static final int maxInterval = 10_000;

    private static final MathFunction LINEAR_FUNCTION = x -> 2 * x + 3;

    public static void main(String[] args) throws InterruptedException {

        TabulatedFunction f = new ArrayTabulatedFunction(LINEAR_FUNCTION, 1, 4, 10_000_000);
        ForkJoinPool forkJoinPool = createForkJoinPool();
        Params params = createStartParametrs();
        Interval interval = new Interval(0, f.getCount()-1);
        ForkJoinTask<Double> task = new Integrate(f, params, interval);

        long start2 = System.currentTimeMillis();

        ForkJoinTask<Double> result = forkJoinPool.submit(task);

        Double d = result.join();

        long t2 = System.currentTimeMillis() - start2;

        System.out.println("time: "+ t2);
    }

    static Params createStartParametrs() {
        return new Params(amountOfThreads, maxInterval);
    }

    static ForkJoinPool createForkJoinPool(){
        return new ForkJoinPool(amountOfThreads);
    }
}

