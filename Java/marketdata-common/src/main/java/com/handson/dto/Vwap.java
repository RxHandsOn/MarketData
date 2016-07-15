package com.handson.dto;

import com.google.gson.Gson;

public class Vwap {

    public final String code;
    public final double vwap;
    public final double volume;

    public Vwap(String code) {
        this.code = code;
        this.vwap = 0;
        this.volume = 0;
    }

    public Vwap(String code, double vwap, double volume) {
        this.code = code;
        this.vwap = vwap;
        this.volume = volume;
    }

    public Vwap addTrade(Trade t) {
        double volume = this.volume + t.quantity;
        double vwap = (this.volume * this.vwap + t.nominal) / volume;
        return new Vwap(t.code, vwap, volume);
    }

    public static Vwap fromJson(String input) {
        return new Gson().fromJson(input, Vwap.class);
    }


    public String toJson() {
        return new Gson().toJson(this);
    }

    @Override
    public String toString() {
        return toJson();
    }
}
