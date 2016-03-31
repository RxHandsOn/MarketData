package com.handson.infra;

import io.netty.buffer.ByteBuf;
import io.reactivex.netty.RxNetty;
import io.reactivex.netty.pipeline.PipelineConfigurators;
import io.reactivex.netty.protocol.http.client.HttpClient;
import io.reactivex.netty.protocol.http.client.HttpClientRequest;
import io.reactivex.netty.protocol.http.client.HttpClientResponse;
import io.reactivex.netty.protocol.http.sse.ServerSentEvent;
import rx.Observable;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class RxNettyEventEventStreamClient implements EventStreamClient {



    private final int port;
    private final Observable<String> serverSideEvents;

    public RxNettyEventEventStreamClient(int port) {
        this.port = port;
        this.serverSideEvents = initializeStream();
    }

    private Observable<String> initializeStream() {
        HttpClient<ByteBuf, ServerSentEvent> client =
                RxNetty.createHttpClient("localhost", port, PipelineConfigurators.<ByteBuf>clientSseConfigurator());

        return client.submit(HttpClientRequest.createGet("/hello")).
                flatMap(response -> {
                    printResponseHeader(response);
                    return response.getContent();
                }).map(serverSentEvent -> serverSentEvent.contentAsString());
    }

    public Observable<String> readServerSideEvents() {
        return serverSideEvents;
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


}
