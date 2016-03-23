import * as rx from 'rxjs/Rx.KitchenSink';
import * as TestScheduler from 'rxjs/testing/TestScheduler';
import {SubscriptionLog} from 'rxjs/testing/SubscriptionLog';
import { expect } from 'chai';

declare const global: any;
declare let rxTestScheduler: rx.TestScheduler;

beforeEach(() => {
    global.rxTestScheduler = new rx.TestScheduler((actual: any, expected: any): void => {
        expect(actual).to.deep.equal(expected);
        return;
    });
});

export function cold<T>(marbles: string, values?: any, error?: any): rx.Observable<T> {
    if (!rxTestScheduler) {
        throw 'tried to use cold() in async test';
    }
    return rxTestScheduler.createColdObservable<T>(marbles, values, error);
}

export function hot<T>(marbles: string, values?: any, error?: any): rx.Observable<T> {
    if (!rxTestScheduler) {
        throw 'tried to use cold() in async test';
    }
    return rxTestScheduler.createHotObservable<T>(marbles, values, error);
}

export function expectObservable(observable: rx.Observable<any>,
                                 unsubscriptionMarbles: string = null): ({ toBe: TestScheduler.observableToBeFn }) {
    if (!rxTestScheduler) {
        throw 'tried to use cold() in async test';
    }
    return rxTestScheduler.expectObservable(observable, unsubscriptionMarbles);
}

export function expectSubscriptions(actualSubscriptionLogs: SubscriptionLog[]): ({ toBe: TestScheduler.subscriptionLogsToBeFn }) {
    if (!global.rxTestScheduler) {
        throw 'tried to use expectSubscriptions() in async test';
    }
    return global.rxTestScheduler.expectSubscriptions.apply(global.rxTestScheduler, arguments);
}

export function time(marbles: string): number {
    if (!rxTestScheduler) {
        throw 'tried to use time() in async test';
    }
    return rxTestScheduler.createTime.apply(rxTestScheduler, arguments);
}
