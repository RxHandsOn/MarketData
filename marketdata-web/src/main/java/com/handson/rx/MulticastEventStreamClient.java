package com.handson.rx;

import com.handson.infra.EventStreamClient;
import rx.Observable;

public class MulticastEventStreamClient implements EventStreamClient {

    private final Observable<String> serverSideEvents;

    public MulticastEventStreamClient(EventStreamClient targetClient) {
        /* Etape initiale
        this.serverSideEvents = targetClient.readServerSideEvents(); */
        this.serverSideEvents = targetClient.readServerSideEvents().publish().refCount();
    }

    @Override
    public Observable<String> readServerSideEvents() {
        return serverSideEvents;
    }

}
