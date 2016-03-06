package com.handson.rx;


import com.handson.dto.Quote;
import com.handson.dto.Trade;
import com.handson.dto.Vwap;
import com.handson.infra.EventStreamClient;
import com.handson.infra.RxNettyEventServer;
import rx.Observable;
import rx.Scheduler;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class VwapServer extends RxNettyEventServer<Vwap> {

    private final EventStreamClient tradeEventStreamClient;
    private final Scheduler scheduler;

    public VwapServer(int port, EventStreamClient tradeEventStreamClient, Scheduler scheduler) {
        super(port);
        this.tradeEventStreamClient = tradeEventStreamClient;
        this.scheduler = scheduler;
    }

    @Override
    protected Observable<Vwap> getEvents(Map<String, List<String>> parameters) {
        List<String> companies = parameters.get("STOCK");

        return tradeEventStreamClient.readServerSideEvents()
                .doOnEach(System.out::println)
                .map(Trade::fromJson)
                .filter((trade -> companies.contains(trade.code)))
                .scan(new Vwap(), ((vwapSum, trade) -> {
                    double volume = vwapSum.volume + trade.quantity;
                    double vwap = ((vwapSum.vwap * vwapSum.volume) + trade.nominal) / volume;
                    return new Vwap(trade.code, vwap, volume);
                })).skip(1);
    }
}
