package com.handson.market;

import com.handson.infra.RandomSequenceGenerator;
import com.handson.infra.RxNettyEventServer;
import rx.Observable;

import java.util.concurrent.TimeUnit;

public class DealProvider extends RxNettyEventServer<String> {

    public static final int PORT = 8097;
    private static final int DEFAULT_INTERVAL = 250;
    private static final int GOOGLE_MIN = 695;
    private static final int GOOGLE_MAX = 710;

    private final int interval;
    private final TimeUnit intervalUnit;



    public DealProvider(int port, int interval, TimeUnit intervalUnit) {
        super(port);
        this.interval = interval;
        this.intervalUnit = intervalUnit;
    }

    @Override
    protected Observable<String> getEvents() {
        Observable<Double> googleStock
                = new RandomSequenceGenerator(GOOGLE_MIN, GOOGLE_MAX).create(interval, intervalUnit).share();
        Observable<Integer> quantities
                = new RandomSequenceGenerator(10, 1000).createIntegerSequence(interval, intervalUnit).share();
        Observable<Double> nominals
                = quantities.zipWith(googleStock, (q, s) -> q * s);
        Observable<String> messages
                = quantities.zipWith(nominals, (q, n) -> "{ \"quantity\" : " + q + " , \"nominal\" : " + n + " }");
        return messages;
    }

    public static void main(String[] args) {
        new DealProvider(PORT, DEFAULT_INTERVAL, TimeUnit.MILLISECONDS).createServer().startAndWait();
    }
}
