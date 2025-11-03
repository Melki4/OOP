package ru.ssau.tk._repfor2lab_._OOP_.concurrent;

import ru.ssau.tk._repfor2lab_._OOP_.functions.LinkedListTabulatedFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.MathFunction;
import ru.ssau.tk._repfor2lab_._OOP_.functions.UnitFunction;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiplyingTaskExecutor {

    static MathFunction unitFunction = new UnitFunction();
    public static int AmountOfThreads = 16;
    static LinkedListTabulatedFunction listTabulatedFunction = new LinkedListTabulatedFunction(unitFunction, 1.0, 1000, 1000);

    public static void main(String[] s) throws InterruptedException {

        CountDownLatch countDownLatch = new CountDownLatch(AmountOfThreads);
        ExecutorService executor = Executors.newFixedThreadPool(8);

        for (int i =0; i<AmountOfThreads;++i){
            executor.submit(() -> {
                try{
                    createMultiplayingTask().run();
                } finally {
                     countDownLatch.countDown();
                }
            }
            );
        }

        try {
            countDownLatch.await();
        }
        catch (InterruptedException e){
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }

        System.out.println(listTabulatedFunction);

        executor.shutdown();
    }

    public static MultiplyingTask createMultiplayingTask(){
        return new MultiplyingTask(listTabulatedFunction);
    }
}