package com.handson.infra;

import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.server.HttpServer;
import io.reactivex.netty.protocol.http.sse.ServerSentEvent;
import rx.Observable;

/**
 * Strongly inspired from RxNetty's examples
 */
public abstract class RxNettyEventBroadcaster<T> extends RxNettyEventServer {


    private final boolean flaky;
    private Observable<T> events;

    public RxNettyEventBroadcaster(int port, boolean flaky) {
        super(port);
        this.flaky = flaky;
    }

    public HttpServer<ByteBuf, ServerSentEvent> createServer() {
        if (flaky) {
            events = SubscriptionLimiter
                        .limitSubscriptions(1,initializeEventStream());
        } else {
            events  = initializeEventStream();
        }
        return super.createServer();
    }

    protected abstract Observable<T> initializeEventStream();

    public Observable<T> getEvents(HttpRequest request) {
        return events;
    }

}
