package com.handson.rx;

import com.handson.dto.Quote;
import com.handson.infra.EventStreamClient;
import com.handson.infra.RxNettyEventServer;
import rx.Observable;
import rx.subjects.BehaviorSubject;
import rx.subjects.ReplaySubject;

import java.util.List;
import java.util.Map;

import static rx.Observable.combineLatest;


public class StockServer extends RxNettyEventServer<Quote> {

    private final EventStreamClient stockEventStreamClient;
    private final EventStreamClient forexEventStreamClient;

    public StockServer(int port, EventStreamClient stockEventStreamClient, EventStreamClient forexEventStreamClient) {
        super(port);
        this.stockEventStreamClient = stockEventStreamClient;
        this.forexEventStreamClient = forexEventStreamClient;
    }

    @Override
    protected Observable<Quote> getEvents(Map<String, List<String>> parameters) {
        Observable<Quote> stockEvents = stockEventStreamClient.readServerSideEvents()
                .map(Quote::fromJson)
                .filter(quote -> parameters.get("STOCK").contains(quote.code))
                .doOnEach(System.out::println);

        Observable<Quote> forexEvents = forexEventStreamClient.readServerSideEvents()
                .map(Quote::fromJson)
                .doOnEach(System.out::println);

        BehaviorSubject<Quote> forexEventsCached = BehaviorSubject.create();
        forexEvents.subscribe(forexEventsCached);

        return stockEvents.flatMap(stockQuote -> forexEventsCached.take(1).map(forexQuote ->
                new Quote(stockQuote.code, stockQuote.quote / forexQuote.quote)));
    }
}
