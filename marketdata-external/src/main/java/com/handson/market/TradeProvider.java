package com.handson.market;

import com.handson.dto.Quote;
import com.handson.dto.Trade;
import com.handson.infra.RandomSequenceGenerator;
import com.handson.infra.RxNettyEventBroadcaster;
import rx.Observable;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class TradeProvider extends RxNettyEventBroadcaster<Trade> {

    private static final int INTERVAL = 50;
    private final StockQuoteProvider stockQuoteProvider;


    public TradeProvider(int port, StockQuoteProvider stockQuoteProvider) {
        super(port);
        this.stockQuoteProvider = stockQuoteProvider;
    }

    @Override
    protected Observable<Trade> initializeEventStream() {
        Observable<Quote> quotes
                = stockQuoteProvider.getEvents(null);
        Observable<Integer> quantities
                = new RandomSequenceGenerator(10, 1000).createIntegerSequence(INTERVAL, TimeUnit.MILLISECONDS).share();

        return quotes.flatMap(quote -> {
            return quantities
                    .take(1)
                    .map(qty -> new Trade(quote.code, qty, quote.quote * qty));
        });
    }

}
