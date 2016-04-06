package com.handson.rx;

import com.handson.dto.Quote;
import com.handson.infra.EventStreamClient;
import com.handson.infra.HttpRequest;
import com.handson.infra.RxNettyEventServer;
import rx.Observable;
import rx.Scheduler;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ForexServer extends RxNettyEventServer<Double> {


    private final EventStreamClient forexEventStreamClient;

    public ForexServer(int port, EventStreamClient eventStreamClient) {
        super(port);
        this.forexEventStreamClient = eventStreamClient;
    }

    @Override
    protected Observable<Double> getEvents(HttpRequest request) {
        return Observable.never();
    }
}
