package com.handson.rx;

import com.handson.infra.*;
import rx.Scheduler;
import rx.schedulers.Schedulers;

import java.io.IOException;


public class Application {

    public static void main(String[] args) throws IOException {
        new StaticServer("marketdata-web", 8000).createServer().start();

        // if ever you need a static http server for the documentation...
        // new StaticServer("reactivex.io-mirror", 8001).createServer().start();

        Scheduler scheduler = Schedulers.computation();
        EventStreamClient forexEventStreamClient
                = new MulticastEventStreamClient(new RxNettyEventEventStreamClient(8096), scheduler);
        ForexServer forexServer = new ForexServer(8080, forexEventStreamClient);
        forexServer.createServer().start();

        EventStreamClient stockEventStreamClient
                = new MulticastEventStreamClient(new RxNettyEventEventStreamClient(8097), scheduler);
        StockQuoteServer stockQuoteServer
                = new StockQuoteServer(8081, stockEventStreamClient, forexEventStreamClient, scheduler);
        stockQuoteServer.createServer().start();

        EventStreamClient tradeEventStreamClient
                = new MulticastEventStreamClient(new RxNettyEventEventStreamClient(8098), scheduler);
        VwapServer vwapServer = new VwapServer(8082, tradeEventStreamClient, scheduler);
        vwapServer.createServer().start();

        RequestReplyClient stockStaticDataClient = new RxNettyRequestReplyClient(8099, "code");
        StockServer stockServer
                = new StockServer(8083, stockStaticDataClient, stockEventStreamClient, scheduler);
        stockServer.createServer().start();

        System.out.println("Servers ready!");
        System.out.println("Application available on http://localhost:8000");
        System.out.println("Press any key to exit");
        System.in.read();
    }



}
