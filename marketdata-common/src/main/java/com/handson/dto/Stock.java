package com.handson.dto;

import com.google.gson.Gson;

public class Stock {

    public String code;

    public String companyName;

    public String market;

    public Stock() {
    }

    public Stock(String code, String companyName, String market) {
        this.code = code;
        this.companyName = companyName;
        this.market = market;
    }

    public static Stock fromJson(String input) {
        return new Gson().fromJson(input, Stock.class);
    }


    public String toJson() {
        return new Gson().toJson(this);
    }

    @Override
    public String toString() {
        return toJson();
    }
}


