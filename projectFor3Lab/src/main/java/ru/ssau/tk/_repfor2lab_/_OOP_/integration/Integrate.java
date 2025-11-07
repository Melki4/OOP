package ru.ssau.tk._repfor2lab_._OOP_.integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ssau.tk._repfor2lab_._OOP_.functions.TabulatedFunction;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import static java.lang.Math.max;

public class Integrate extends RecursiveTask<Double> {

    private final TabulatedFunction function;
    private final Interval interval;
    private final Params params;
    private static final Logger LOGGER = LoggerFactory.getLogger(Integrate.class);

    public Integrate(TabulatedFunction f, Params params, Interval interval) {
        function = f;
        this.interval = interval;
        this.params = params;
        params.maxInterval = max(params.maxInterval, (interval.length())/100);
    }

    @Override
    protected Double compute() {
        Double result;
        if (interval.length() < params.maxInterval || params.forkFactor == 1) {
            result = bruteForce();
        } else {
            result = divideAndConquer();
        }
        return result;
    }

    private Double divideAndConquer() {
        LOGGER.trace("Starting splitting massive");
        int i = (interval.b() - interval.a()) / 2;

        Integrate task1 = new Integrate(function, params, new Interval(interval.a(), interval.a() + i));
        Integrate task2 = new Integrate(function, params, new Interval(interval.a() + i + 1, interval.b()));

        ForkJoinTask.invokeAll(task1, task2);

        Double res = 0.0;

        res+=task2.join();
        res+=task1.join();
        LOGGER.trace("Ending splitting massive");
        return res;
    }

    private Double bruteForce() {
        LOGGER.trace("Starting calculating");
        double sum = 0;

        double step = (function.getX(interval.b()) - function.getX(interval.a())) / (interval.length() - 1);

        for (int i = 0; i < interval.length(); i++) {
            if (i == 0 || i == interval.length()-1) {

                sum += 0.5 * function.getY(interval.a()+i);
            } else {
                sum += function.getY(interval.a()+i);
            }
        }
        LOGGER.trace("Ending calculating");
        return step * sum;
    }
}
