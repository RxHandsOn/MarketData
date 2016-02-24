package com.handson.rx;


import com.handson.infra.Client;
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

    @Test
    public void should_forward_forex_data() {
        // given
        Client client = mock(Client.class);
        TestScheduler scheduler = Schedulers.test();
        ForexServer forexServer = new ForexServer(42, client, scheduler);
        TestSubject<String> testSubject = TestSubject.create(scheduler);
        when(client.readServerSideEvents()).thenReturn(testSubject);
        TestSubscriber<Double> testSubscriber = new TestSubscriber<>();
        forexServer.getEvents(null).subscribe(testSubscriber);
        // when
        testSubject.onNext("1.4");
        scheduler.advanceTimeBy(2, TimeUnit.SECONDS);
        // then
        List<Double> events = testSubscriber.getOnNextEvents();
        assertThat(events).hasSize(1);
        assertThat(events.get(0)).isEqualTo(1.4);
    }

    @Test
    public void should_drop_data_and_keep_only_one_price_each_sec() {
        // given
        Client client = mock(Client.class);
        TestScheduler scheduler = Schedulers.test();
        ForexServer forexServer = new ForexServer(42, client, scheduler);
        TestSubject<String> testSubject = TestSubject.create(scheduler);
        when(client.readServerSideEvents()).thenReturn(testSubject);
        TestSubscriber<Double> testSubscriber = new TestSubscriber<>();
        forexServer.getEvents(null).subscribe(testSubscriber);
        // when
        for (int i = 0; i < 10; i++) {
            testSubject.onNext("1." + i, i);
        }
        scheduler.advanceTimeBy(2, TimeUnit.SECONDS);
        // then
        List<Double> events = testSubscriber.getOnNextEvents();
        assertThat(events).hasSize(1);
        assertThat(events.get(0)).isEqualTo(1.9);
    }


}