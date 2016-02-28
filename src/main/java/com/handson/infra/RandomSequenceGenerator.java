package com.handson.infra;


import rx.Observable;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class RandomSequenceGenerator {

    private final double min;
    private final double max;
    private final Random random;
    private double bias = 0.3;

    public RandomSequenceGenerator(double min, double max) {
        this.min = min;
        this.max = max;
        this.random = new Random();
    }

    public Observable<Double> create(long interval, TimeUnit timeUnit) {
        double init = (min + max) /2;
        return Observable
                .interval(interval, timeUnit)
                .scan(init, (previous, i) -> computeNextNumber(previous));
    }

    public Observable<Integer> createIntegerSequence(long interval, TimeUnit timeUnit) {
        double range = max - min;
        return Observable
                .interval(interval, timeUnit)
                .map(i ->  ((Double)(random.nextDouble() * range  + min)).intValue());
    }

    public double computeNextNumber(double previous) {
        double range = (max - min) / 20;
        double scaled = (random.nextDouble() - 0.5 + bias) * range;
        double shifted = previous + scaled;
        if (shifted < min || shifted > max) {
            shifted = previous - scaled;
            bias = -bias;
        }

        shifted = ((Long)Math.round(shifted * 10000)).doubleValue() /10000;

        return shifted;
    }

}
