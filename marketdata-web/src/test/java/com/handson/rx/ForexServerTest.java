package com.handson.rx;


import com.handson.dto.Quote;
import com.handson.infra.EventStreamClient;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;
import rx.subjects.TestSubject;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ForexServerTest {

    private EventStreamClient eventStreamClient;
    private TestScheduler scheduler;
    private ForexServer forexServer;
    private TestSubject<String> forexSourceSubject;

    @Before
    public void setUpServer() {
        eventStreamClient = mock(EventStreamClient.class);
        scheduler = Schedulers.test();
        forexServer = new ForexServer(42, eventStreamClient);
        forexSourceSubject = TestSubject.create(scheduler);
        when(eventStreamClient.readServerSideEvents()).thenReturn(forexSourceSubject);
    }

    /**
     * Test 1
     */
    @Test
    @Ignore
    public void should_forward_forex_data() {
        // given
        TestSubscriber<Double> testSubscriber = new TestSubscriber<>();
        forexServer.getEvents(null).subscribe(testSubscriber);
        // when
        forexSourceSubject.onNext(new Quote("EUR/USD", 1.4).toJson());
        scheduler.advanceTimeBy(2, TimeUnit.SECONDS);
        // then
        List<Double> events = testSubscriber.getOnNextEvents();
        assertThat(events).hasSize(1);
        assertThat(events.get(0)).isEqualTo(1.4);
    }

    /**
     * Test 2
     */
    @Test
    @Ignore
    public void should_forward_only_one_forex_data() {
        // given
        TestSubscriber<Double> testSubscriber = new TestSubscriber<>();
        forexServer.getEvents(null).subscribe(testSubscriber);
        // when
        forexSourceSubject.onNext(new Quote("EUR/USD", 1.4).toJson());
        forexSourceSubject.onNext(new Quote("EUR/USD", 1.42).toJson());
        scheduler.advanceTimeBy(2, TimeUnit.SECONDS);
        // then
        List<Double> events = testSubscriber.getOnNextEvents();
        assertThat(events).hasSize(1);
        assertThat(events.get(0)).isEqualTo(1.4);
    }

}