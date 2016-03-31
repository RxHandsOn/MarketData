package com.handson.rx;

import com.handson.infra.EventStreamClient;
import org.junit.Test;
import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;
import rx.subjects.PublishSubject;
import rx.subjects.TestSubject;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class MulticastEventStreamClientTest {

    @Test
    public void Should_transmit_events_from_the_target_client() {
        // given
        TestScheduler scheduler = Schedulers.test();
        PublishSubject<String> subject = PublishSubject.create();
        EventStreamClient targetClient = mock(EventStreamClient.class);
        when(targetClient.readServerSideEvents()).thenReturn(subject);
        MulticastEventStreamClient multicastEventStreamClient = new MulticastEventStreamClient(targetClient, scheduler);
        Observable<String> events = multicastEventStreamClient.readServerSideEvents();
        // when
        TestSubscriber<String> subscriber = new TestSubscriber<>();
        events.subscribe(subscriber);
        subject.onNext("Hello!");
        // then
        assertThat(subscriber.getOnNextEvents()).hasSize(1).contains("Hello!");
    }

    /**
     * Test 21
     */
    @Test
    public void Should_generate_only_one_subscription_side_effect_with_multiple_subscribers() {
        // given
        Observable<String> source = Observable.create(subscriber -> {
            subscriber.onNext("open");
        });
        TestScheduler scheduler = Schedulers.test();
        EventStreamClient targetClient = mock(EventStreamClient.class);
        when(targetClient.readServerSideEvents()).thenReturn(source);
        MulticastEventStreamClient multicastEventStreamClient = new MulticastEventStreamClient(targetClient, scheduler);
        Observable<String> events = multicastEventStreamClient.readServerSideEvents();
        // when
        TestSubscriber<String> subscriber1 = new TestSubscriber<>();
        TestSubscriber<String> subscriber2 = new TestSubscriber<>();
        events.subscribe(subscriber1);
        events.subscribe(subscriber2);
        // then
        assertThat(subscriber1.getOnNextEvents()).hasSize(1);
        assertThat(subscriber2.getOnNextEvents()).isEmpty();
    }

    /**
     * Test 22
     */
    @Test
    public void Should_wait_2_sec_before_trying_to_reconnect_when_there_is_a_connection_error() {
        // given
        TestScheduler scheduler = Schedulers.test();
        AtomicBoolean firstRetry = new AtomicBoolean(true);
        Observable<String> source = Observable.create(subscriber -> {
            if (firstRetry.getAndSet(false)) {
                subscriber.onError(new Exception("shit happens"));
            }
            subscriber.onNext("Hello!");
        });
        EventStreamClient targetClient = mock(EventStreamClient.class);
        when(targetClient.readServerSideEvents()).thenReturn(source);
        MulticastEventStreamClient multicastEventStreamClient = new MulticastEventStreamClient(targetClient, scheduler);
        Observable<String> events = multicastEventStreamClient.readServerSideEvents();
        TestSubscriber<String> subscriber = new TestSubscriber<>();
        events.subscribe(subscriber);
        // when
        scheduler.advanceTimeBy(2, TimeUnit.SECONDS);
        // then
        assertThat(subscriber.getOnNextEvents()).hasSize(1).contains("Hello!");
    }

}