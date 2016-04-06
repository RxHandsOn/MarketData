package com.handson.dto;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Quote {

    public final String code;
    public final double quote;

    public Quote(String code, double quote) {
        this.code = code;
        this.quote = quote;
    }

    public static Quote fromJson(String input) {
        return new Gson().fromJson(input, Quote.class);
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

    @Override
    public String toString() {
        return toJson();
    }
}
