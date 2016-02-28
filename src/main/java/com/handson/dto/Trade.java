package com.handson.dto;

import com.google.gson.GsonBuilder;

public class Trade {

    public String code;
    public int quantity;
    public double nominal;

    public Trade() {
    }

    public Trade(String code, int quantity, double nominal) {
        this.code = code;
        this.quantity = quantity;
        this.nominal = nominal;
    }

    public static Trade fromJson(String input) {
        return new GsonBuilder().create().fromJson(input, Trade.class);
    }


    public String toJson() {
        return new GsonBuilder().create().toJson(this);
    }

    @Override
    public String toString() {
        return toJson();
    }
}
