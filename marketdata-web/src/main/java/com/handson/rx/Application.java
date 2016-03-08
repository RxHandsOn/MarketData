package com.handson.rx;

import com.handson.infra.EventStreamClient;
import com.handson.infra.RxNettyEventEventStreamClient;
import rx.schedulers.Schedulers;

import java.io.IOException;


public class Application {

    public static void main(String[] args) throws IOException {
        EventStreamClient forexEventStreamClient = new RxNettyEventEventStreamClient(8096);
        ForexServer forexServer = new ForexServer(8080, forexEventStreamClient);
        forexServer.createServer().start();

        EventStreamClient stockEventStreamClient = new RxNettyEventEventStreamClient(8097);

        StockQuoteServer stockQuoteServer = new StockQuoteServer(8081, stockEventStreamClient, forexEventStreamClient);
        stockQuoteServer.createServer().start();

        EventStreamClient tradeEventStreamClient = new RxNettyEventEventStreamClient(8098);
        VwapServer vwapServer = new VwapServer(8082, tradeEventStreamClient, Schedulers.immediate());
        vwapServer.createServer().start();

        System.out.println("Servers ready!");
        System.out.println("Press any key to exit");
        //new RxNettyEventEventStreamClient(8080).readServerSideEvents().toBlocking().forEach(System.out::println);
        System.in.read();
    }
}
