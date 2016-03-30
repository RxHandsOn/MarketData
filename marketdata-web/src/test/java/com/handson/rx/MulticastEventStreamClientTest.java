package com.handson.rx;

import com.handson.infra.EventStreamClient;
import org.junit.Test;
import rx.Observable;
import rx.observers.TestSubscriber;
import rx.subjects.PublishSubject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class MulticastEventStreamClientTest {

    @Test
    public void Should_transmit_events_from_the_target_client() {
        // given
        PublishSubject<String> subject = PublishSubject.create();
        EventStreamClient targetClient = mock(EventStreamClient.class);
        when(targetClient.readServerSideEvents()).thenReturn(subject);
        MulticastEventStreamClient multicastEventStreamClient = new MulticastEventStreamClient(targetClient);
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
        EventStreamClient targetClient = mock(EventStreamClient.class);
        when(targetClient.readServerSideEvents()).thenReturn(source);
        MulticastEventStreamClient multicastEventStreamClient = new MulticastEventStreamClient(targetClient);
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

}