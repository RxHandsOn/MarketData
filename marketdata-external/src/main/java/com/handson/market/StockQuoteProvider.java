package com.handson.market;


import com.handson.dto.Quote;
import com.handson.infra.RandomSequenceGenerator;
import com.handson.infra.RxNettyEventBroadcaster;
import rx.Observable;

import java.util.concurrent.TimeUnit;

public class StockQuoteProvider extends RxNettyEventBroadcaster<Quote> {

    public static final int PORT = 8098;

    private static final double GOOGLE_MIN = 695;
    private static final double GOOGLE_MAX = 710;
    private static final double IBM_MIN = 120;
    private static final double IBM_MAX = 135;
    private static final double HP_MIN = 12.5;
    private static final double HP_MAX = 14;
    private static final double APPLE_MIN = 94;
    private static final double APPLE_MAX = 99;
    private static final double MICROSOFT_MIN = 51;
    private static final double MICROSOFT_MAX = 53;


    public StockQuoteProvider(int port, boolean flaky) {
        super(port, flaky);
    }

    @Override
    protected Observable<Quote> initializeEventStream() {
        Observable<Quote> googleStock
                = new RandomSequenceGenerator(GOOGLE_MIN, GOOGLE_MAX)
                    .create(200, TimeUnit.MILLISECONDS)
                    .map(s -> new Quote("GOOGL", s));
        Observable<Quote> ibmStock
                = new RandomSequenceGenerator(IBM_MIN, IBM_MAX)
                    .create(705, TimeUnit.MILLISECONDS)
                    .map(s -> new Quote("IBM", s));
        Observable<Quote> hpStock
                = new RandomSequenceGenerator(HP_MIN, HP_MAX)
                    .create(602, TimeUnit.MILLISECONDS)
                    .map(s -> new Quote("HP", s));
        Observable<Quote> appleStock
                = new RandomSequenceGenerator(APPLE_MIN, APPLE_MAX)
                    .create(253, TimeUnit.MILLISECONDS)
                    .map(s -> new Quote("AAPL", s));
        Observable<Quote> microsoftStock
                = new RandomSequenceGenerator(MICROSOFT_MIN, MICROSOFT_MAX)
                    .create(407, TimeUnit.MILLISECONDS)
                    .map(s -> new Quote("MICROSOFT", s));

        return Observable.merge(googleStock, ibmStock, hpStock, appleStock, microsoftStock).share();
    }

    public static void main(String[] args) {
        new StockQuoteProvider(PORT, false).createServer().startAndWait();
    }
}
