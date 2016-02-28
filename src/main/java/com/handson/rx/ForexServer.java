package com.handson.rx;

import com.handson.dto.Quote;
import com.handson.infra.EventStreamClient;
import com.handson.infra.RxNettyEventServer;
import rx.Observable;
import rx.Scheduler;

import java.util.List;
import java.util.Map;

public class ForexServer extends RxNettyEventServer<Double> {


    private final EventStreamClient forexEventStreamClient;

    public ForexServer(int port, EventStreamClient eventStreamClient) {
        super(port);
        this.forexEventStreamClient = eventStreamClient;
    }

    @Override
    protected Observable<Double> getEvents(Map<String, List<String>> parameters) {
        //return forexClient.readServerSideEvents().map(Double::parseDouble);
        /* TODO etape 1 - map  avec quote  */
        return forexEventStreamClient
                .readServerSideEvents()
                .map(Quote::fromJson)
                .map(q -> q.quote);
    }
}
