package com.handson.rx;


import com.handson.dto.Quote;
import com.handson.dto.Stock;
import com.handson.infra.EventStreamClient;
import com.handson.infra.RequestReplyClient;
import org.junit.Before;
import org.junit.Test;
import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;
import rx.subjects.TestSubject;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StockServerTest {

    private RequestReplyClient stockClient;
    private EventStreamClient stockQuoteClient;
    private TestScheduler scheduler;
    private StockServer stockServer;
    private TestSubject<String> quoteSourceSubject;

    @Before
    public void setUpServer() {
        stockClient = mock(RequestReplyClient.class);
        stockQuoteClient = mock(EventStreamClient.class);
        scheduler = Schedulers.test();
        stockServer = new StockServer(42, stockClient, stockQuoteClient, scheduler);
        quoteSourceSubject = TestSubject.create(scheduler);
        when(stockQuoteClient.readServerSideEvents()).thenReturn(quoteSourceSubject);
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
    }

    /**
     * Test 7
     */
    @Test
    public void should_send_a_stock_message_when_receiving_a_quote() {
        // given
        TestSubscriber<Stock> testSubscriber = new TestSubscriber<>();
        stockServer.getEvents(null).subscribe(testSubscriber);
        // when
        quoteSourceSubject.onNext(new Quote("GOOGL", 705.8673).toJson());
        scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        // then
        List<Stock> events = testSubscriber.getOnNextEvents();
        assertThat(events).hasSize(1);
        assertThat(events.get(0).companyName).isEqualTo("Alphabet Inc");
    }

    /**
     * Test 8
     */
    @Test
    public void should_send_a_stock_message_only_once_when_receiving_two_quotes_for_the_same_stock() {
        // given
        TestSubscriber<Stock> testSubscriber = new TestSubscriber<>();
        stockServer.getEvents(null).subscribe(testSubscriber);
        // when
        quoteSourceSubject.onNext(new Quote("GOOGL", 705.8673).toJson());
        quoteSourceSubject.onNext(new Quote("GOOGL", 705.8912).toJson(), 20);
        quoteSourceSubject.onNext(new Quote("IBM", 126.344).toJson(), 110);

        scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        // then
        List<Stock> events = testSubscriber.getOnNextEvents();
        assertThat(events).hasSize(2);
        assertThat(events.get(0).companyName).isEqualTo("Alphabet Inc");
        assertThat(events.get(1).companyName).isEqualTo("International Business Machines Corp.");
    }

    /**
     * Test 9
     */
    @Test
    public void should_stop_stream_after_10_seconds() {
        // given
        TestSubscriber<Stock> testSubscriber = new TestSubscriber<>();
        stockServer.getEvents(null).subscribe(testSubscriber);
        // when
        scheduler.advanceTimeBy(10, TimeUnit.SECONDS);
        // then
        assertThat(testSubscriber.getOnCompletedEvents()).hasSize(1);
    }
}