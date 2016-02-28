package com.handson.dto;

import com.google.gson.GsonBuilder;

public class Quote {

    public String code;
    public double quote;

    public Quote() {
    }

    public Quote(String code, double quote) {
        this.code = code;
        this.quote = quote;
    }

    public static Quote fromJson(String input) {
        return new GsonBuilder().create().fromJson(input, Quote.class);
    }

    public String toJson() {
        // TODO update GSON version
        return new GsonBuilder().create().toJson(this);
    }

    @Override
    public String toString() {
        return toJson();
    }
}
