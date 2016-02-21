package com.handson;

import io.netty.buffer.ByteBuf;
import io.reactivex.netty.RxNetty;
import io.reactivex.netty.pipeline.PipelineConfigurators;
import io.reactivex.netty.protocol.http.client.HttpClient;
import io.reactivex.netty.protocol.http.client.HttpClientRequest;
import io.reactivex.netty.protocol.http.client.HttpClientResponse;
import io.reactivex.netty.protocol.http.sse.ServerSentEvent;
import rx.Observable;
import rx.functions.Func1;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class RxNettyEventClient {

    static final int DEFAULT_NO_OF_EVENTS = 100;
    static final int DEFAULT_PORT = 8096;


    private final int port;
    private final int noOfEvents;

    public RxNettyEventClient(int port, int noOfEvents) {
        this.port = port;
        this.noOfEvents = noOfEvents;
    }

    public List<ServerSentEvent> readServerSideEvents() {
        HttpClient<ByteBuf, ServerSentEvent> client =
                RxNetty.createHttpClient("localhost", port, PipelineConfigurators.<ByteBuf>clientSseConfigurator());

        Iterable<ServerSentEvent> eventIterable = client.submit(HttpClientRequest.createGet("/hello")).
                flatMap(response -> {
                    printResponseHeader(response);
                    return response.getContent();
                }).take(noOfEvents).doOnNext(serverSentEvent -> System.out.println(serverSentEvent.contentAsString())).toBlocking().toIterable();

        List<ServerSentEvent> events = new ArrayList<>();
        for (ServerSentEvent event : eventIterable) {
            System.out.println(event);
            events.add(event);
        }

        return events;
    }

    private static void printResponseHeader(HttpClientResponse<ServerSentEvent> response) {
        System.out.println("New response received.");
        System.out.println("========================");
        System.out.println(response.getHttpVersion().text() + ' ' + response.getStatus().code()
                + ' ' + response.getStatus().reasonPhrase());
        for (Map.Entry<String, String> header : response.getHeaders().entries()) {
            System.out.println(header.getKey() + ": " + header.getValue());
        }
    }

    public static void main(String[] args) {
        new RxNettyEventClient(DEFAULT_PORT, DEFAULT_NO_OF_EVENTS).readServerSideEvents();
    }

}
