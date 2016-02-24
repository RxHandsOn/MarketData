package com.handson.market;


import com.handson.infra.RandomSequenceGenerator;
import com.handson.infra.RxNettyEventBroadcaster;
import rx.Observable;

import java.util.concurrent.TimeUnit;

public class StockProvider extends RxNettyEventBroadcaster<String> {

    public static final int PORT = 8098;
    private static final int DEFAULT_INTERVAL = 1000;

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

    private final int interval;
    private final TimeUnit intervalUnit;



    public StockProvider(int port, int interval, TimeUnit intervalUnit) {
        super(port);
        this.interval = interval;
        this.intervalUnit = intervalUnit;
    }

    @Override
    protected Observable<String> initializeEventStream() {
        Observable<String> googleStock
                = new RandomSequenceGenerator(GOOGLE_MIN, GOOGLE_MAX)
                    .create(interval, intervalUnit)
                    .map(s -> "{ \"code\" : \"GOOGLE\", \"quote\" : " + s  + " }");
        Observable<String> ibmStock
                = new RandomSequenceGenerator(IBM_MIN, IBM_MAX)
                    .create(interval, intervalUnit)
                    .map(s -> "{ \"code\" : \"IBM\", \"quote\" : " + s  + " }");
        Observable<String> hpStock
                = new RandomSequenceGenerator(HP_MIN, HP_MAX)
                    .create(interval, intervalUnit)
                    .map(s -> "{ \"code\" : \"HP\", \"quote\" : " + s  + " }");
        Observable<String> appleStock
                = new RandomSequenceGenerator(APPLE_MIN, APPLE_MAX)
                    .create(interval, intervalUnit)
                    .map(s -> "{ \"code\" : \"APPLE\", \"quote\" : " + s  + " }");
        Observable<String> microsoftStock
                = new RandomSequenceGenerator(MICROSOFT_MIN, MICROSOFT_MAX)
                    .create(interval, intervalUnit)
                    .map(s -> "{ \"code\" : \"MICROSOFT\", \"quote\" : " + s  + " }");

        return Observable.merge(googleStock, ibmStock, hpStock, appleStock, microsoftStock);
    }

    public static void main(String[] args) {
        new StockProvider(PORT, DEFAULT_INTERVAL, TimeUnit.MILLISECONDS).createServer().startAndWait();
    }
}
