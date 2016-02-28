package com.handson.dto;

import com.google.gson.GsonBuilder;

public class Vwap {

    public String code;
    public double vwap;
    public double volume;

    public Vwap() {
    }

    public Vwap(String code, double vwap, double volume) {
        this.code = code;
        this.vwap = vwap;
        this.volume = volume;
    }

    public static Vwap fromJson(String input) {
        return new GsonBuilder().create().fromJson(input, Vwap.class);
    }


    public String toJson() {
        return new GsonBuilder().create().toJson(this);
    }

    @Override
    public String toString() {
        return toJson();
    }
}
