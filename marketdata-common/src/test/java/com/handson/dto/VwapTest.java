package com.handson.dto;


import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class VwapTest {


    @Test
    public void should_match_trade_volume_and_nominal_when_only_one_trade() {
        // given
        Vwap vwap = new Vwap("IBM", 0, 0);
        // when
        Vwap vwap2 = vwap.addTrade(new Trade("IBM", 10, 420));
        // then
        assertThat(vwap2.volume).isEqualTo(10);
        assertThat(vwap2.vwap).isEqualTo(42);
    }

    @Test
    public void should_compute_average_when_adding_a_trade() {
        // given
        Vwap vwap = new Vwap("IBM", 700, 10);
        // when
        Vwap vwap2 = vwap.addTrade(new Trade("IBM", 20, 15200));
        // then
        assertThat(vwap2.volume).isEqualTo(30);
        assertThat(vwap2.vwap).isEqualTo(740);
    }
}