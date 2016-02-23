package com.handson.market;


import com.handson.infra.RandomSequenceGenerator;
import com.handson.infra.RxNettyEventServer;
import com.handson.infra.SubscriptionLimiter;
import rx.Observable;

import java.util.concurrent.TimeUnit;

public class ForexProvider extends RxNettyEventServer<Double> {

    public static final int PORT = 8096;
    private static final int DEFAULT_INTERVAL = 100;

    private final int interval;
    private final TimeUnit intervalUnit;



    public ForexProvider(int port, int interval, TimeUnit intervalUnit) {
        super(port);
        this.interval = interval;
        this.intervalUnit = intervalUnit;
    }

    @Override
    protected Observable<Double> getEvents() {
        System.out.println("Creating a market stream limited to only one subscription");
        return SubscriptionLimiter.limitSubscriptions(1, new RandomSequenceGenerator(1.2, 1.3).create(interval, intervalUnit));
    }

    public static void main(String[] args) {
        new ForexProvider(PORT, DEFAULT_INTERVAL, TimeUnit.MILLISECONDS).createServer().startAndWait();
    }
}
