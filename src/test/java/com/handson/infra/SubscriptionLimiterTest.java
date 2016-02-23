package com.handson.infra;


import org.junit.Test;
import rx.Observable;
import rx.Subscription;
import rx.observers.TestSubscriber;
import rx.subjects.PublishSubject;

import static org.assertj.core.api.Assertions.assertThat;

public class SubscriptionLimiterTest {

    @Test
    public void should_allow_one_subscription() {
        // given
        PublishSubject<Integer> subject = PublishSubject.create();
        Observable<Integer> limitedObservable = SubscriptionLimiter.limitSubscriptions(1, subject);
        TestSubscriber<Integer> subscriber = new TestSubscriber<>();
        // when
        limitedObservable.subscribe(subscriber);
        subject.onNext(123);
        // then
        assertThat(subscriber.getOnNextEvents()).hasSize(1).contains(123);
    }

    @Test
    public void should_fail_on_second_subscription() {
        // given
        PublishSubject<Integer> subject = PublishSubject.create();
        Observable<Integer> limitedObservable = SubscriptionLimiter.limitSubscriptions(1, subject);
        TestSubscriber<Integer> subscriber = new TestSubscriber<>();
        TestSubscriber<Integer> subscriber2 = new TestSubscriber<>();
        limitedObservable.subscribe(subscriber);
        // when
        limitedObservable.subscribe(subscriber2);
        subject.onNext(123);
        // then
        assertThat(subscriber2.getOnNextEvents()).isEmpty();
        assertThat(subscriber2.getOnErrorEvents()).hasSize(1);

    }

    @Test
    public void should_allow_a_subscription_after_an_unsubscription() {
        // given
        PublishSubject<Integer> subject = PublishSubject.create();
        Observable<Integer> limitedObservable = SubscriptionLimiter.limitSubscriptions(1, subject);
        TestSubscriber<Integer> subscriber = new TestSubscriber<>();
        TestSubscriber<Integer> subscriber2 = new TestSubscriber<>();
        Subscription subscription = limitedObservable.subscribe(subscriber);
        // when
        subscription.unsubscribe();
        limitedObservable.subscribe(subscriber2);
        subject.onNext(123);
        // then
        assertThat(subscriber2.getOnNextEvents()).hasSize(1).contains(123);
    }



}