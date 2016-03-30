package com.handson.rx;

import com.handson.dto.Quote;
import com.handson.dto.Stock;
import com.handson.infra.EventStreamClient;
import com.handson.infra.HttpRequest;
import com.handson.infra.RequestReplyClient;
import com.handson.infra.RxNettyEventServer;
import rx.Observable;
import rx.Scheduler;

import java.util.concurrent.TimeUnit;


public class StockServer extends RxNettyEventServer<Stock> {

    private final RequestReplyClient stockClient;
    private final EventStreamClient quoteEventStreamClient;
    private final Scheduler scheduler;

    public StockServer(int port,
                       RequestReplyClient stockClient,
                       EventStreamClient quoteEventStreamClient,
                       Scheduler scheduler) {
        super(port);
        this.stockClient = stockClient;
        this.quoteEventStreamClient = quoteEventStreamClient;
        this.scheduler = scheduler;
    }

    @Override
    protected Observable<Stock> getEvents(HttpRequest request) {

        /* Etape initiale
        return Observable.never(); */

        /* Etape 1
        return quoteEventStreamClient
                .readServerSideEvents()
                .map(Quote::fromJson)
                .flatMap(quote ->
                            stockClient
                                .request(quote.code)
                                .doOnNext(System.out::println)
                                .map(Stock::fromJson)
                );
        */

        /* Etape 2
        return quoteEventStreamClient
                .readServerSideEvents()
                .map(Quote::fromJson)
                .map(quote -> quote.code)
                .distinct()
                .flatMap(code ->
                        stockClient
                                .request(code)
                                .doOnNext(System.out::println)
                                .map(Stock::fromJson)
                );
        */

        /* Etape 3 */
        return quoteEventStreamClient
                .readServerSideEvents()
                .map(Quote::fromJson)
                .map(quote -> quote.code)
                .distinct()
                .flatMap(code ->
                        stockClient
                                .request(code)
                                .doOnNext(System.out::println)
                                .map(Stock::fromJson)
                ).takeUntil(Observable.timer(10, TimeUnit.SECONDS, scheduler));

    }
}
