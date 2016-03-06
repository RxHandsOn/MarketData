package com.handson.infra;

import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.server.HttpServer;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import io.reactivex.netty.protocol.http.sse.ServerSentEvent;
import rx.Observable;

import java.util.List;
import java.util.Map;

/**
 * Strongly inspired from RxNetty's examples
 */
public abstract class RxNettyEventBroadcaster<T> extends RxNettyEventServer {


    private Observable<T> events;

    public RxNettyEventBroadcaster(int port) {
        super(port);
    }

    public HttpServer<ByteBuf, ServerSentEvent> createServer() {
        events  = initializeEventStream();
        return super.createServer();
    }

    protected abstract Observable<T> initializeEventStream();

    public Observable<T> getEvents(Map parameters) {
        return events;
    }

}
