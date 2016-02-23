package com.handson.forex;


import com.handson.infra.RandomSequenceGenerator;
import com.handson.infra.RxNettyEventServer;
import com.handson.infra.SubscriptionLimiter;
import rx.Observable;

import java.util.concurrent.TimeUnit;

public class ForexServer extends RxNettyEventServer<Double> {

    static final int DEFAULT_PORT = 8096;
    static final int DEFAULT_INTERVAL = 1000;

    private final int interval;
    private final TimeUnit intervalUnit;



    public ForexServer(int port, int interval, TimeUnit intervalUnit) {
        super(port);
        this.interval = interval;
        this.intervalUnit = intervalUnit;
    }

    @Override
    protected Observable<Double> getEvents() {
        System.out.println("Creating a forex stream limited to only one subscription");
        return SubscriptionLimiter.limitSubscriptions(1, new RandomSequenceGenerator(1.3, 1.2).create(interval, intervalUnit));
    }

    public static void main(String[] args) {
        new ForexServer(DEFAULT_PORT, DEFAULT_INTERVAL, TimeUnit.MILLISECONDS).createServer().startAndWait();
    }
}
