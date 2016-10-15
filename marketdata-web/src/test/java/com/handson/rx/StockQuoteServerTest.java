package com.handson.rx;

import com.handson.dto.Quote;
import com.handson.infra.EventStreamClient;
import com.handson.infra.HttpRequest;
import org.junit.Rule;
import org.junit.Test;
import rx.Observable;
import rx.marble.HotObservable;
import rx.marble.junit.MarbleRule;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static rx.marble.MapHelper.of;
import static rx.marble.junit.MarbleRule.*;

public class StockQuoteServerTest {

    @Rule
    public MarbleRule marble = new MarbleRule(1000);

    /**
     * Test 3
     */
    @Test
    public void should_filter_quotes_for_requested_stock() {
        // given
        Observable<String> quoteSource
                = hot("--(ga)--", of("g", new Quote("GOOGL", 705.8673).toJson(),
                                     "a", new Quote("APPLE", 98.18).toJson()));
        Observable<String> forexSource
                = hot("--f--", of("f", new Quote("EUR/USD", 1).toJson()));
        // when
        StockQuoteServer stockQuoteServer = createServer(quoteSource, forexSource);
        HttpRequest request = createRequest("code", "GOOGL");
        // then
        expectObservable(stockQuoteServer.getEvents(request).map(q -> q.code))
                .toBe("--v--", of("v", "GOOGL"));
    }

    /**
     * Test 13
     */
    @Test
    public void should_generate_one_quote_in_euro_for_one_quote_in_dollar() {
        // given
        Observable<String> quoteSource
                = hot("--g----", of("g", new Quote("GOOGL", 1300).toJson()));
        Observable<String> forexSource
                = hot("--f-x--", of("f", new Quote("EUR/USD", 1.3).toJson(),
                                    "x", new Quote("EUR/USD", 1.4).toJson()));
        // when
        StockQuoteServer stockQuoteServer = createServer(quoteSource, forexSource);
        HttpRequest request = createRequest("code", "GOOGL");
        // then
        expectObservable(stockQuoteServer.getEvents(request).map(q -> q.quote))
                .toBe("--v----", of("v", 1000d));
    }

    /**
     * Test 14
     */
    @Test
    public void should_generate_quotes_in_euro_using_latest_known_foreign_exchange_rate() {
        // given
        Observable<String> quoteSource
                = hot("----g-", of("g", new Quote("GOOGL", 1300).toJson()));
        Observable<String> forexSource
                = hot("-f-x--", of("f", new Quote("EUR/USD", 1.2).toJson(),
                                    "x", new Quote("EUR/USD", 1.3).toJson()));
        // when
        StockQuoteServer stockQuoteServer = createServer(quoteSource, forexSource);
        HttpRequest request = createRequest("code", "GOOGL");
        // then
        expectObservable(stockQuoteServer.getEvents(request).map(q -> q.quote))
                .toBe("----v-", of("v", 1000d));
    }

    /**
     * Test 15
     */
    @Test
    public void should_unsubscribe_to_forex_stream_when_unscribing_to_quote() {
        // given
        Observable<String> quoteSource
                = hot("----g---", of("g", new Quote("GOOGL", 1300).toJson()));
        HotObservable<String> forexSource
                = hot("---f----", of("f", new Quote("EUR/USD", 1.2).toJson()));
        Observable<String> stopSource
                = hot("------s-");
        // when
        StockQuoteServer stockQuoteServer = createServer(quoteSource, forexSource);
        HttpRequest request = createRequest("code", "GOOGL");
        stockQuoteServer.getEvents(request).takeUntil(stopSource).subscribe();
        // then
        expectSubscriptions(forexSource.getSubscriptions())
                .toBe("^-----!-");
    }

    /**
     * Test 16
     */
    @Test
    public void should_send_an_error_when_no_forex_data_after_fifty_milliseconds() {
        // given
        Observable<String> quoteSource
                = hot("-g-----", of("g", new Quote("GOOGL", 1300).toJson()));
        Observable<String> forexSource
                = hot("-------");
        // when
        StockQuoteServer stockQuoteServer = createServer(quoteSource, forexSource);
        HttpRequest request = createRequest("code", "GOOGL");
        // then
        expectObservable(stockQuoteServer.getEvents(request))
                .toBe("------#");
    }

    public HttpRequest createRequest(String name, String value) {
        return new HttpRequest(Collections.singletonMap(name, Arrays.asList(value)));
    }

    public StockQuoteServer createServer(Observable<String> quoteSource, Observable<String> forexSource) {
        EventStreamClient stockQuoteEventStreamClient = mock(EventStreamClient.class);
        EventStreamClient forexEventStreamClient = mock(EventStreamClient.class);
        StockQuoteServer stockQuoteServer
                = new StockQuoteServer(42, stockQuoteEventStreamClient, forexEventStreamClient, marble.scheduler);
        when(stockQuoteEventStreamClient.readServerSideEvents()).thenReturn(quoteSource);
        when(forexEventStreamClient.readServerSideEvents()).thenReturn(forexSource);
        return stockQuoteServer;
    }
}