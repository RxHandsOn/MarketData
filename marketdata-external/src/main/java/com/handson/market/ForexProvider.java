package com.handson.market;


import com.handson.infra.RandomSequenceGenerator;
import com.handson.infra.RxNettyEventBroadcaster;
import com.handson.infra.SubscriptionLimiter;
import com.handson.dto.Quote;
import rx.Observable;

import java.util.concurrent.TimeUnit;


public class ForexProvider extends RxNettyEventBroadcaster<Quote> {

    private static final int INTERVAL = 500;

    public ForexProvider(int port) {
        super(port);
    }

    @Override
    protected Observable<Quote> initializeEventStream() {
        // TODO à déplacer sur les services pour le front
        System.out.println("Creating a market stream limited to only one subscription");
        return SubscriptionLimiter
                .limitSubscriptions(1, new RandomSequenceGenerator(1.2, 1.3).create(INTERVAL, TimeUnit.MILLISECONDS))
                .map(q -> new Quote("EUR/USD", q));
    }
}
