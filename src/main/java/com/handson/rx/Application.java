package com.handson.rx;

import com.handson.infra.EventStreamClient;
import com.handson.infra.RxNettyEventEventStreamClient;

import java.io.IOException;


public class Application {

    public static void main(String[] args) throws IOException {
        EventStreamClient forexEventStreamClient = new RxNettyEventEventStreamClient(8096);
        ForexServer forexServer = new ForexServer(8080, forexEventStreamClient);
        forexServer.createServer().start();
        System.out.println("Press any key to exit");
        new RxNettyEventEventStreamClient(8080).readServerSideEvents().toBlocking().forEach(System.out::println);
        System.in.read();

        // TODO
        // changer frequences par stock
        // cache dernier prix
        // serveur vwap
        // marge / flatmap


    }
}
