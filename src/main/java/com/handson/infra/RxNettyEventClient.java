package com.handson.infra;

import io.netty.buffer.ByteBuf;
import io.reactivex.netty.RxNetty;
import io.reactivex.netty.pipeline.PipelineConfigurators;
import io.reactivex.netty.protocol.http.client.HttpClient;
import io.reactivex.netty.protocol.http.client.HttpClientRequest;
import io.reactivex.netty.protocol.http.client.HttpClientResponse;
import io.reactivex.netty.protocol.http.sse.ServerSentEvent;
import rx.Observable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class RxNettyEventClient implements Client {

    static final int DEFAULT_PORT = 8096;


    private final int port;

    public RxNettyEventClient(int port) {
        this.port = port;
    }

    public Observable<String> readServerSideEvents() {
        HttpClient<ByteBuf, ServerSentEvent> client =
                RxNetty.createHttpClient("localhost", port, PipelineConfigurators.<ByteBuf>clientSseConfigurator());

        return client.submit(HttpClientRequest.createGet("/hello")).
                flatMap(response -> {
                    printResponseHeader(response);
                    return response.getContent();
                }).map(serverSentEvent -> serverSentEvent.contentAsString());
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
        new RxNettyEventClient(DEFAULT_PORT).readServerSideEvents().toBlocking().forEach(System.out::println);
    }

}
