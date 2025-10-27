package ru.ssau.tk._repfor2lab_._OOP_.integration;

import ru.ssau.tk._repfor2lab_._OOP_.functions.ArrayTabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.MathFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.Point;
import ru.ssau.tk._repfor2lab_._OOP_.functions.TabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.operations.TabulatedFunctionOperationService;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

public class Test {

    private static int forkFactor = 16;
    private static ForkJoinPool forkJoinPool = new ForkJoinPool(forkFactor);

    private static final MathFunction LINEAR_FUNCTION = x -> 2 * x + 3;

    public static void main(String[] args) throws InterruptedException {

        TabulatedFunction f = new ArrayTabulatedFunction(LINEAR_FUNCTION, 1, 4, 10000000);

        // Оцениваем общее количество задач (можно улучшить эту оценку)

        int r = 10000;

        Params params = new Params(forkFactor, r);

        Point[] points = TabulatedFunctionOperationService.asPoints(f);

        ForkJoinTask<Double> task = new Integrate(0, f.getCount()-1, f, params, points);

        long start2 = System.currentTimeMillis();

        ForkJoinTask<Double> result = forkJoinPool.submit(task);

        Double d =result.join();

        long t2 = System.currentTimeMillis() - start2;

        System.out.println("time: "+ t2);
    }
}

