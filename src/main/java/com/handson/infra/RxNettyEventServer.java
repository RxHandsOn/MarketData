package com.handson.infra;

import io.netty.buffer.ByteBuf;
import io.reactivex.netty.RxNetty;
import io.reactivex.netty.pipeline.PipelineConfigurators;
import io.reactivex.netty.protocol.http.server.HttpServer;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import io.reactivex.netty.protocol.http.server.HttpServerResponse;
import io.reactivex.netty.protocol.http.sse.ServerSentEvent;
import rx.Notification;
import rx.Observable;
import rx.functions.Func1;

import java.util.List;
import java.util.Map;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;

/**
 * Strongly inspired from RxNetty's examples
 */
public abstract class RxNettyEventServer<T> {


    private final int port;

    public RxNettyEventServer(int port) {
        this.port = port;
    }

    public HttpServer<ByteBuf, ServerSentEvent> createServer() {
        HttpServer<ByteBuf, ServerSentEvent> server = RxNetty.createHttpServer(port,
                (request, response) -> {
                    response.getHeaders().set(ACCESS_CONTROL_ALLOW_ORIGIN, "*");
                    response.getHeaders().set(CACHE_CONTROL, "no-cache");
                    response.getHeaders().set(CONNECTION, "keep-alive");
                    response.getHeaders().set(CONTENT_TYPE, "text/event-stream");
                    return getIntervalObservable(request, response);
                }, PipelineConfigurators.<ByteBuf>serveSseConfigurator());
        System.out.println("HTTP Server Sent Events server started...");
        return server;
    }

    private Observable<Void> getIntervalObservable(HttpServerRequest<?> request, final HttpServerResponse<ServerSentEvent> response) {
        Map<String, List<String>> parameters = request.getQueryParameters();
        return getEvents(parameters)
                .flatMap(event -> {
                    System.out.println("Writing SSE event: " + event);
                    ByteBuf data = response.getAllocator().buffer().writeBytes(( event + "\n").getBytes());
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


    protected abstract Observable<T> getEvents(Map<String, List<String>> queryParameters);


}
