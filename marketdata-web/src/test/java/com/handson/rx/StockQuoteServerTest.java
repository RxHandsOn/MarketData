package com.handson.rx;


import com.handson.dto.Quote;
import com.handson.infra.EventStreamClient;
import com.handson.infra.HttpRequest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import rx.Subscription;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;
import rx.subjects.TestSubject;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StockQuoteServerTest {

    private EventStreamClient stockQuoteEventStreamClient;
    private EventStreamClient forexEventStreamClient;
    private TestScheduler scheduler;
    private StockQuoteServer stockQuoteServer;
    private TestSubject<String> quoteSourceSubject;
    private TestSubject<String> forexSourceSubject;

    @Before
    public void setUpServer() {
        stockQuoteEventStreamClient = mock(EventStreamClient.class);
        forexEventStreamClient = mock(EventStreamClient.class);
        scheduler = Schedulers.test();
        stockQuoteServer = new StockQuoteServer(42, stockQuoteEventStreamClient, forexEventStreamClient, scheduler);
        quoteSourceSubject = TestSubject.create(scheduler);
        when(stockQuoteEventStreamClient.readServerSideEvents()).thenReturn(quoteSourceSubject);
        forexSourceSubject = TestSubject.create(scheduler);
        when(forexEventStreamClient.readServerSideEvents()).thenReturn(forexSourceSubject);
    }

    /**
     * Test 3
     */
    @Test
    @Ignore
    public void should_filter_quotes_for_requested_stock() {
        // given
        TestSubscriber<Quote> testSubscriber = new TestSubscriber<>();
        HttpRequest request = createRequest("code", "GOOGL");
        stockQuoteServer.getEvents(request).subscribe(testSubscriber);
        // when
        quoteSourceSubject.onNext(new Quote("GOOGL", 705.8673).toJson());
        quoteSourceSubject.onNext(new Quote("APPLE", 98.18).toJson());
        scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        // then
        List<Quote> events = testSubscriber.getOnNextEvents();
        assertThat(events).hasSize(1);
        assertThat(events.get(0).code).isEqualTo("GOOGL");
    }

    /**
     * Test 13
     */
    @Test
    @Ignore
    public void should_generate_one_quote_in_euro_for_one_quote_in_dollar() {
        // given
        TestSubscriber<Quote> testSubscriber = new TestSubscriber<>();
        HttpRequest request = createRequest("code", "GOOGL");
        stockQuoteServer.getEvents(request).subscribe(testSubscriber);
        // when
        quoteSourceSubject.onNext(new Quote("GOOGL", 1300).toJson());
        forexSourceSubject.onNext(new Quote("EUR/USD", 1.3).toJson());
        forexSourceSubject.onNext(new Quote("EUR/USD", 1.4).toJson());
        scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        // then
        List<Quote> events = testSubscriber.getOnNextEvents();
        assertThat(events).hasSize(1);
        assertThat(events.get(0).quote).isEqualTo(1000);
    }

    /**
     * Test 14
     */
    @Test
    @Ignore
    public void should_generate_quotes_in_euro_using_latest_known_foreign_exchange_rate() {
        // given
        TestSubscriber<Quote> testSubscriber = new TestSubscriber<>();
        HttpRequest request = createRequest("code", "GOOGL");
        stockQuoteServer.getEvents(request).subscribe(testSubscriber);
        // when
        forexSourceSubject.onNext(new Quote("EUR/USD", 1.2).toJson(), 80);
        forexSourceSubject.onNext(new Quote("EUR/USD", 1.3).toJson(), 90);
        quoteSourceSubject.onNext(new Quote("GOOGL", 1300).toJson(), 100);
        scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        // then
        List<Quote> events = testSubscriber.getOnNextEvents();
        assertThat(events).hasSize(1);
        assertThat(events.get(0).quote).isEqualTo(1000);
    }


    /**
     * Test 15
     */
    @Test
    @Ignore
    public void should_unsubscribe_to_forex_stream_when_unscribing_to_quote() {
        // given
        TestSubscriber<Quote> testSubscriber = new TestSubscriber<>();
        HttpRequest request = createRequest("code", "GOOGL");
        Subscription subscription = stockQuoteServer.getEvents(request).subscribe(testSubscriber);
        forexSourceSubject.onNext(new Quote("EUR/USD", 1.3).toJson(), 90);
        quoteSourceSubject.onNext(new Quote("GOOGL", 1300).toJson(), 100);
        scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        // when
        subscription.unsubscribe();
        // then
        assertThat(forexSourceSubject.hasObservers()).isFalse();
    }

    /**
     * Test 16
     */
    @Test
    @Ignore
    public void should_send_an_error_when_no_forex_data_after_five_seconds() {
        // given
        TestSubscriber<Quote> testSubscriber = new TestSubscriber<>();
        HttpRequest request = createRequest("code", "GOOGL");
        stockQuoteServer.getEvents(request).subscribe(testSubscriber);
        quoteSourceSubject.onNext(new Quote("GOOGL", 1300).toJson(), 100);
        // when
        scheduler.advanceTimeBy(5100, TimeUnit.MILLISECONDS);
        // then
        assertThat(testSubscriber.getOnErrorEvents()).hasSize(1);
    }

    public HttpRequest createRequest(String name, String value) {
        return new HttpRequest(Collections.singletonMap(name, Arrays.asList(value)));
    }
}