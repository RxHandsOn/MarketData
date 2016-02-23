package com.handson.rx;

import com.handson.infra.Client;
import com.handson.infra.RxNettyEventServer;
import rx.Observable;

import java.util.concurrent.TimeUnit;

public class ForexServer extends RxNettyEventServer<Double> {


    private final Client client;

    public ForexServer(int port, Client client) {
        super(port);
        this.client = client;
    }

    @Override
    protected Observable<Double> getEvents() {
        return client.readServerSideEvents().map(Double::parseDouble).sample(2, TimeUnit.SECONDS);
    }
}
