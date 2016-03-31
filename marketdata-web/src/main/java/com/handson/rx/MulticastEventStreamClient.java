package com.handson.rx;

import com.handson.infra.EventStreamClient;
import rx.Observable;
import rx.Scheduler;

import java.util.concurrent.TimeUnit;

public class MulticastEventStreamClient implements EventStreamClient {

    private final Observable<String> serverSideEvents;
    private final Scheduler scheduler;

    public MulticastEventStreamClient(EventStreamClient targetClient, Scheduler scheduler) {
        this.scheduler = scheduler;
       this.serverSideEvents = targetClient.readServerSideEvents();
    }

    @Override
    public Observable<String> readServerSideEvents() {
        return serverSideEvents;
    }

}
