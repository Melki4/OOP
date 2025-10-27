package ru.ssau.tk._repfor2lab_._OOP_.integration;

import ru.ssau.tk._repfor2lab_._OOP_.functions.Point;
import ru.ssau.tk._repfor2lab_._OOP_.functions.TabulatedFunction;

import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

import static java.lang.Math.max;

public class Integrate extends RecursiveTask<Double> {

    private final TabulatedFunction function;
    private final int a;
    private final int b;
    private final int size;
    private final Params params;
    private final Point[] points;

    public Integrate(int a, int b, TabulatedFunction f, Params params, Point[] points) {
        function = f;
        this.a = a;
        size = b-a+1;
        this.b = b;
        this.params = params;
        params.maxInterval = max(params.maxInterval, (size)/100);
        this.points = points;
    }

    @Override
    protected Double compute() {
        Double result;
        if (size < params.maxInterval || params.forkFactor == 1) {
            result = bruteForce();
        } else {
            result = divideAndConquer();
        }
        // Увеличиваем счетчик и проверяем завершение
        return result;
    }

    private Double divideAndConquer() {

        int i = (b - a) / 2;

        Integrate task1 = new Integrate(a, a + i, function, params, points);
        Integrate task2 = new Integrate(a + i +1, b, function, params, points);

        ForkJoinTask.invokeAll(task1, task2);

        Double res = 0.0;

        res+=task2.join();

        res+=task1.join();

        return res;
    }

    private Double bruteForce() {
//        System.out.println("Thread " + Thread.currentThread());
        double sum = 0;
        double step = (points[b].x - points[a].x) / (size - 1);

        for (int i = 0; i < size; i++) {
            if (i == 0 || i == size-1) {
                sum += 0.5 * points[a + i].y;
            } else {
                sum += points[a + i].y;
            }
        }
        return step * sum;
    }
}
