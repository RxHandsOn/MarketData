package com.handson.rx;


import com.handson.dto.Trade;
import com.handson.dto.Vwap;
import com.handson.infra.EventStreamClient;
import com.handson.infra.HttpRequest;
import com.handson.infra.RxNettyEventServer;
import rx.Observable;
import rx.Scheduler;

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
    protected Observable<Vwap> getEvents(HttpRequest request) {
        String stockCode = request.getParameter("code");
        return Observable.never();
    }
}
