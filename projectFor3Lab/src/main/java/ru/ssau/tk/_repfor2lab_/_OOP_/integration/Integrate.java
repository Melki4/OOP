package ru.ssau.tk._repfor2lab_._OOP_.integration;

import ru.ssau.tk._repfor2lab_._OOP_.functions.Point;
import ru.ssau.tk._repfor2lab_._OOP_.functions.TabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.operations.TabulatedFunctionOperationService;

import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

import static java.lang.Math.max;

public class Integrate extends RecursiveTask<Double> {

    private final TabulatedFunction function;
    private final Interval interval;
    private final Params params;
    private final Point[] points;

    public Integrate(Interval interval, TabulatedFunction f, Params params) {
        function = f;
        this.interval = interval;
        this.params = params;
        params.maxInterval = max(params.maxInterval, interval.length()/100);
        points = TabulatedFunctionOperationService.asPoints(function);
    }

    @Override
    protected Double compute() {
        Double result;
        if (interval.length() < params.maxInterval || params.forkFactor == 1) {
            result = bruteForce();
        } else {
            result = divideAndConquer();
        }
        // Увеличиваем счетчик и проверяем завершение
        return result;
    }

    private Double divideAndConquer() {

        int i = (interval.b() - interval.a()) / 2;

        Integrate task1 = new Integrate(new Interval(interval.a(), interval.a() + i), function, params);
        Integrate task2 = new Integrate(new Interval(interval.a() + i +1, interval.b()), function, params);

        ForkJoinTask.invokeAll(task1, task2);

        Double res = 0.0;

        res+=task2.join();

        res+=task1.join();

        return res;
    }

    private Double bruteForce() {

        int leftIndex = interval.a();
        int rightIndex = interval.b();

        int count = rightIndex - leftIndex + 1;

        double sum = 0;
        double h = (points[rightIndex].x - points[leftIndex].x) / (count - 1);

        for (int i = 0; i < count; i++) {
            if (i == 0 || i == count-1) {
                sum += 0.5 * points[leftIndex + i].y;
            } else {
                sum += points[leftIndex + i].y;
            }
        }
        return h * sum;
    }
}
