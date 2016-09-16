package com.handson.market;


import com.handson.infra.RandomSequenceGenerator;
import com.handson.infra.RxNettyEventBroadcaster;
import com.handson.dto.Quote;
import rx.Observable;

import java.util.concurrent.TimeUnit;


public class ForexProvider extends RxNettyEventBroadcaster<Quote> {

    private static final int INTERVAL = 500;

    public ForexProvider(int port, boolean flaky) {
        super(port, flaky);
    }

    @Override
        protected Observable<Quote> initializeEventStream() {
            return new RandomSequenceGenerator(1.2, 1.3)
                    .create(INTERVAL, TimeUnit.MILLISECONDS)
                    .map(q -> new Quote("EUR/USD", q));
    }
}
