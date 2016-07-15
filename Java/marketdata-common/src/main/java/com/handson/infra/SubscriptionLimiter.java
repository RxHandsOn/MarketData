package com.handson.infra;

import rx.Observable;
import rx.Subscription;

import java.util.ArrayList;
import java.util.List;

public class SubscriptionLimiter {



    public static <T> Observable<T> limitSubscriptions(int maxNumber, Observable<T> source) {
        State state = new State();
        return Observable.create(subscriber -> {
            synchronized (state) {
                if (state.getNumberOfSubscriptions() == maxNumber) {
                    System.out.println("Subscription not allowed");
                    subscriber.onError(new RuntimeException("Number of subscription limited to " + maxNumber));
                } else {
                    Subscription subscription = source.subscribe(subscriber);
                    state.add(subscription);
                }
            }
        });
    }

    private static class State {

        private List<Subscription> subscriptions = new ArrayList<>();

        private int getNumberOfSubscriptions() {
            cleanUpDeadSubscriptions();
            return subscriptions.size();
        }

        private void cleanUpDeadSubscriptions() {
            List<Subscription> deadSubscriptions = new ArrayList<>();
            subscriptions.stream().forEach(subscription -> {
                if (subscription.isUnsubscribed()) {
                    deadSubscriptions.add(subscription);
                }
            });
            deadSubscriptions.stream().forEach(subscription -> {
                subscriptions.remove(subscription);
            });
        }

        private void add(Subscription subscription) {
            subscriptions.add(subscription);
        }

    }
}
