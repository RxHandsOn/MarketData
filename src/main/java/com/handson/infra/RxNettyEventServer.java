package com.handson.infra;

import io.reactivex.netty.RxNetty;
import io.reactivex.netty.pipeline.PipelineConfigurators;
import io.reactivex.netty.protocol.http.server.HttpServer;

import java.util.concurrent.TimeUnit;

import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.server.HttpServerResponse;
import io.reactivex.netty.protocol.http.sse.ServerSentEvent;
import rx.Notification;
import rx.Observable;
import rx.functions.Func1;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;

/**
 * Strongly inspired from RxNetty's examples
 */
public class RxNettyEventServer {

    static final int DEFAULT_PORT = 8096;
    static final int DEFAULT_INTERVAL = 1000;

    private final int port;
    private final int interval;

    public RxNettyEventServer(int port, int interval) {
        this.port = port;
        this.interval = interval;
    }

    public HttpServer<ByteBuf, ServerSentEvent> createServer() {
        HttpServer<ByteBuf, ServerSentEvent> server = RxNetty.createHttpServer(port,
                (request, response) -> {
                    response.getHeaders().set(ACCESS_CONTROL_ALLOW_ORIGIN, "*");
                    response.getHeaders().set(CACHE_CONTROL, "no-cache");
                    response.getHeaders().set(CONNECTION, "keep-alive");
                    response.getHeaders().set(CONTENT_TYPE, "text/event-stream");
                    return getIntervalObservable(response);
                }, PipelineConfigurators.<ByteBuf>serveSseConfigurator());
        System.out.println("HTTP Server Sent Events server started...");
        return server;
    }

    private Observable<Void> getIntervalObservable(final HttpServerResponse<ServerSentEvent> response) {
        return getEvents()
                .flatMap(event -> {
                    System.out.println("Writing SSE event for interval: " + event);
                    ByteBuf data = response.getAllocator().buffer().writeBytes(("hello " + event + "\n").getBytes());
                    ServerSentEvent sse = new ServerSentEvent(data);
                    return response.writeAndFlush(sse);
                }).materialize()
                .takeWhile(notification -> {
                    if (notification.isOnError()) {
                        System.out.println("Write to client failed, stopping response sending.");
                        notification.getThrowable().printStackTrace(System.err);
                    }
                    return !notification.isOnError();
                })
                .map((Func1<Notification<Void>, Void>) notification -> null);
    }

    protected Observable<Double> getEvents() {
        return new RandomSequenceGenerator(1.3, 1.2).create(1, TimeUnit.SECONDS);
        //return Observable.interval(interval, TimeUnit.MILLISECONDS);
    }

    public static void main(String[] args) {
        new RxNettyEventServer(DEFAULT_PORT, DEFAULT_INTERVAL).createServer().startAndWait();
    }


}
