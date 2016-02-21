package com.handson;


import rx.Observable;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class RandomSequenceGenerator {

    private final double min;
    private final double max;
    private final Random random;

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

    public double computeNextNumber(double previous) {
        double range = (max - min) / 100;
        double scaled = (random.nextDouble() - 0.5) * range;
        double shifted = previous + scaled;
        if (shifted < min || shifted > max) {
            shifted = previous - scaled;
        }
        return shifted;
    }

}
