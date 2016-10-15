package com.handson.rx;


import com.handson.dto.Quote;
import com.handson.infra.EventStreamClient;
import org.junit.Rule;
import org.junit.Test;
import rx.Observable;
import rx.marble.junit.MarbleRule;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static rx.marble.MapHelper.of;
import static rx.marble.junit.MarbleRule.expectObservable;
import static rx.marble.junit.MarbleRule.hot;


public class ForexServerTest {

    @Rule
    public MarbleRule marble = new MarbleRule();

    public ForexServer create(Observable<String> forexSource) {
        EventStreamClient eventStreamClient = mock(EventStreamClient.class);
        ForexServer forexServer = new ForexServer(42, eventStreamClient);
        when(eventStreamClient.readServerSideEvents()).thenReturn(forexSource);
        return forexServer;
    }

    /**
     * Test 1
     */
    @Test
    public void should_forward_forex_data() {
        // given
        Observable<String> forexSource
                = hot("--f--", of("f", new Quote("EUR/USD", 1.4).toJson()));
        // when
        ForexServer forexServer = create(forexSource);
        // then
        expectObservable(forexServer.getEvents(null))
                .toBe("--(v|)", of("v", 1.4));
    }

    /**
     * Test 2
     */
    @Test
    public void should_forward_only_one_forex_data() {
        // given
        Observable<String> forexSource
                = hot("--f-x-", of("f", new Quote("EUR/USD", 1.4).toJson(),
                                   "x", new Quote("EUR/USD", 1.4).toJson()));
        // when
        ForexServer forexServer = create(forexSource);
        // then
        expectObservable(forexServer.getEvents(null))
                .toBe("--(v|)", of("v", 1.4));
    }

}