package com.handson.market;


import java.io.IOException;

public class Market {

    public static void main(String[] args) throws IOException {
        new ForexProvider(8096).createServer().start();
        StockQuoteProvider stockQuoteProvider = new StockQuoteProvider(8097);
        stockQuoteProvider.createServer().start();
        new TradeProvider(8098, stockQuoteProvider).createServer().start();
        System.out.println("**** Providers ready! ****");
        System.out.println("Press any key to exit");
        System.in.read();

    }

}
