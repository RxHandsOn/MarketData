package com.handson.rx;

import com.handson.dto.Quote;
import com.handson.infra.EventStreamClient;
import com.handson.infra.HttpRequest;
import com.handson.infra.RxNettyEventServer;
import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.subjects.BehaviorSubject;

import java.util.concurrent.TimeUnit;


public class StockQuoteServer extends RxNettyEventServer<Quote> {

    private final EventStreamClient stockQuoteEventStreamClient;
    private final EventStreamClient forexEventStreamClient;
    private final Scheduler scheduler;

    public StockQuoteServer(int port,
                            EventStreamClient stockQuoteEventStreamClient,
                            EventStreamClient forexEventStreamClient,
                            Scheduler scheduler) {
        super(port);
        this.stockQuoteEventStreamClient = stockQuoteEventStreamClient;
        this.forexEventStreamClient = forexEventStreamClient;
        this.scheduler = scheduler;
    }

    @Override
    protected Observable<Quote> getEvents(HttpRequest request) {
        String stockCode = request.getParameter("code");
        return stockQuoteEventStreamClient
            .readServerSideEvents()
            .map(Quote::fromJson);
    }
}
