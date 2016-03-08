package com.handson.market;


import com.handson.dto.Stock;
import com.handson.infra.HttpRequest;
import com.handson.infra.RxNettyReqReplyServer;

import java.util.HashMap;
import java.util.Map;

public class StaticDataProvider extends RxNettyReqReplyServer {

    private final Map<String, Stock> stocks;

    public StaticDataProvider(int port) {
        super(port, "code");

        stocks = new HashMap<>();
        stocks.put("GOOGL", new Stock("GOOGL", "Alphabet Inc", "NASDAQ"));
        stocks.put("IBM", new Stock("IBM", "International Business Machines Corp.", "NYSE"));
        stocks.put("AAPL", new Stock("AAPL", "Apple Inc.", "NASDAQ"));
        stocks.put("HPQ", new Stock("HPQ", "HP Inc", "NYSE"));
        stocks.put("MSFT", new Stock("MSFT", "Microsoft Corporation", "NASDAQ"));

    }

    @Override
    protected String getResponseContent(HttpRequest request) {
        String code = request.getParameter("code");
        if (code == null) {
            throw new RuntimeException("code parameter mandatory");
        }
        Stock stock = stocks.get(code);
        if (stock == null) {
            throw new RuntimeException("code " + code + " not found");
        }
        return stock.toJson();
    }


}

