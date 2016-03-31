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
        /* Etape initiale
        this.serverSideEvents = targetClient.readServerSideEvents(); */
        /* Etape 1
        this.serverSideEvents = targetClient.readServerSideEvents().publish().refCount();
        */
        this.serverSideEvents
                = targetClient
                    .readServerSideEvents()
                    .publish()
                    .refCount()
                    .retryWhen(errors -> errors.delay(2, TimeUnit.SECONDS, scheduler));
    }

    @Override
    public Observable<String> readServerSideEvents() {
        return serverSideEvents;
    }

}
