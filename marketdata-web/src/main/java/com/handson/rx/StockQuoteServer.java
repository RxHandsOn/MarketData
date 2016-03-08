package com.handson.rx;

import com.handson.dto.Quote;
import com.handson.infra.EventStreamClient;
import com.handson.infra.HttpRequest;
import com.handson.infra.RxNettyEventServer;
import rx.Observable;
import rx.subjects.ReplaySubject;

import java.util.List;
import java.util.Map;


public class StockQuoteServer extends RxNettyEventServer<Quote> {

    private final EventStreamClient stockEventStreamClient;
    private final EventStreamClient forexEventStreamClient;

    public StockQuoteServer(int port, EventStreamClient stockEventStreamClient, EventStreamClient forexEventStreamClient) {
        super(port);
        this.stockEventStreamClient = stockEventStreamClient;
        this.forexEventStreamClient = forexEventStreamClient;
    }

    @Override
    protected Observable<Quote> getEvents(HttpRequest request) {

        /* Etape initiale
        String stockCode = request.getParameter("code");
        return stockClient
            .readServerSideEvents()
            .map(Quote::fromJson);
        */

        /* Etape 1 : filtre sur code de la stock
        String stockCode = request.getParameter("code");

        return stockEventStreamClient
                .readServerSideEvents()
                .map(Quote::fromJson)
                .filter(q -> q.code.equals(stockCode));
        */


        /* Etape 2 : application du forex
        String stockCode = request.getParameter("code");

        Observable<Quote> quotes = stockEventStreamClient
                .readServerSideEvents()
                .map(Quote::fromJson)
                .filter(q -> q.code.equals(stockCode))
                .doOnNext(System.out::println);

        Observable<Quote> eurUsd = forexEventStreamClient
                .readServerSideEvents()
                .map(Quote::fromJson)
                .doOnNext(System.out::println);

        return quotes.flatMap(q ->
                eurUsd.take(1).map(fx -> new Quote(q.code, q.quote/fx.quote))
        );
        */

        /* Etape 3 : application forex avec mise en cache */
        String stockCode = request.getParameter("code");

        Observable<Quote> quotes = stockEventStreamClient
                .readServerSideEvents()
                .map(Quote::fromJson)
                .filter(q -> q.code.equals(stockCode))
                .doOnNext(System.out::println);

        Observable<Quote> eurUsd = forexEventStreamClient
                .readServerSideEvents()
                .map(Quote::fromJson)
                .doOnNext(System.out::println);

        ReplaySubject<Quote> eurUsdCached = ReplaySubject.create();
        eurUsd.subscribe(eurUsdCached);

        return quotes.flatMap(q ->
            eurUsdCached.take(1).map(fx -> new Quote(q.code, q.quote/fx.quote))
        );
    }
}
