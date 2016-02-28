package com.handson.rx;

import com.google.gson.GsonBuilder;

public class Price {

    public String code;
    public double buy;
    public double sell;

    public static Price fromJson(String input) {
        return new GsonBuilder().create().fromJson(input, Price.class);
    }

    @Override
    public String toString() {
        return "{ \"code\" : " + code + " , \"buy\" : " + buy + " , \"sell\" : " + sell + " }";
    }
}
