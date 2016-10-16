package com.handson.rx;


import com.handson.dto.Quote;
import com.handson.dto.Stock;
import com.handson.infra.EventStreamClient;
import com.handson.infra.RequestReplyClient;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import rx.Observable;
import rx.marble.junit.MarbleRule;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static rx.marble.MapHelper.of;
import static rx.marble.junit.MarbleRule.expectObservable;
import static rx.marble.junit.MarbleRule.hot;

public class StockServerTest {

    @Rule
    public MarbleRule marble = new MarbleRule(1000);

    /**
     * Test 7
     */
    @Test
    @Ignore
    public void should_send_a_stock_message_when_receiving_a_quote() {
        // given
        Observable<String> quoteSource
                = hot("--q--", of("q", new Quote("GOOGL", 705.8673).toJson()));
        // when
        StockServer server = createServer(quoteSource);
        // then
        expectObservable(server.getEvents(null).map(s -> s.companyName).concatWith(Observable.never()))
                .toBe("--s--", of("s", "Alphabet Inc"));
    }

    /**
     * Test 8
     */
    @Test
    @Ignore
    public void should_send_a_stock_message_only_once_when_receiving_two_quotes_for_the_same_stock() {
        // given
        Observable<String> quoteSource
                = hot("--f-s-t--", of("f", new Quote("GOOGL", 705.8673).toJson(),
                                      "s", new Quote("GOOGL", 705.8912).toJson(),
                                      "t", new Quote("IBM", 106.344).toJson()
        ));
        // when
        StockServer server = createServer(quoteSource);
        // then
        expectObservable(server.getEvents(null).map(s -> s.companyName).concatWith(Observable.never()))
                .toBe("--g---i--", of("g", "Alphabet Inc",
                                      "i", "International Business Machines Corp."));
    }

    /**
     * Test 9
     */
    @Test
    @Ignore
    public void should_stop_stream_after_10_seconds() {
        // given
        Observable<String> quoteSource = Observable.never();
        // when
        StockServer server = createServer(quoteSource);
        // then
        expectObservable(server.getEvents(null)).toBe("----------|");
    }

    public StockServer createServer(Observable<String> quoteSource) {
        RequestReplyClient stockClient = mock(RequestReplyClient.class);
        EventStreamClient stockQuoteClient = mock(EventStreamClient.class);
        StockServer stockServer
                = new StockServer(42, stockClient, stockQuoteClient, marble.scheduler);
        when(stockQuoteClient.readServerSideEvents()).thenReturn(quoteSource);
        when(stockClient.request(eq("GOOGL")))
                .then(
                        code -> Observable.just(
                                new Stock("GOOGL", "Alphabet Inc", "NASDAQ").toJson()
                        )
                );
        when(stockClient.request(eq("IBM")))
                .then(
                        code -> Observable.just(
                                new Stock("IBM", "International Business Machines Corp.", "NYSE").toJson()
                        )
                );
        return stockServer;
    }
}