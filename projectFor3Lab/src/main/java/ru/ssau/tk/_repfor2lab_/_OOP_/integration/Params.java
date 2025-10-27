package ru.ssau.tk._repfor2lab_._OOP_.integration;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.DoubleAdder;

public class Params {

    public final int forkFactor;
    public int maxInterval;


    public Params(int forkFactor, int maxInterval) {
        super();
        this.forkFactor = forkFactor;
        this.maxInterval = maxInterval;
    }
}