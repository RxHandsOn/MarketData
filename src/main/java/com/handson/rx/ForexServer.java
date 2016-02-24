package com.handson.rx;

import com.handson.infra.Client;
import com.handson.infra.RxNettyEventServer;
import rx.Observable;
import rx.Scheduler;

import java.util.concurrent.TimeUnit;

public class ForexServer extends RxNettyEventServer<Double> {


    private final Client client;
    private final Scheduler scheduler;

    public ForexServer(int port, Client client, Scheduler scheduler) {
        super(port);
        this.client = client;
        this.scheduler = scheduler;
    }

    @Override
    protected Observable<Double> getEvents() {
        return client.readServerSideEvents().map(Double::parseDouble).sample(1, TimeUnit.SECONDS, scheduler);
    }
}
