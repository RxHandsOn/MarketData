package com.handson.rx;

import com.handson.infra.Client;
import com.handson.infra.RxNettyEventClient;
import com.handson.market.ForexProvider;
import rx.schedulers.Schedulers;

import java.io.IOException;


public class Application {

    public static void main(String[] args) throws IOException {
        Client forexClient = new RxNettyEventClient(ForexProvider.PORT);
        ForexServer forexServer = new ForexServer(8080, forexClient, Schedulers.immediate());
        forexServer.createServer().start();
        System.out.println("Press any key to exit");
        new RxNettyEventClient(8080).readServerSideEvents().toBlocking().forEach(System.out::println);
        System.in.read();
    }
}
