import * as rx from 'rxjs/Rx.KitchenSink';
import {ArrayObservable} from 'rxjs/observable/ArrayObservable';
import {EmptyObservable} from 'rxjs/observable/EmptyObservable';

declare const rxTestScheduler: rx.TestScheduler;
import {cold, hot, time, expectObservable, expectSubscriptions} from './SchedulerHelper';

const Notification = rx.Notification;
const TestScheduler = rx.TestScheduler;
import { expect } from 'chai';

describe('TestScheduler', () => {
    it('should exist', () => {
        expect(TestScheduler).not.to.be.null;
        expect(typeof TestScheduler).to.equal('function');
    });

    describe('parseMarbles()', () => {
        it('should parse a marble string into a series of notifications and types', () => {
            const result = TestScheduler.parseMarbles('-------a---b---|', {a: 'A', b: 'B'});
            expect(result).to.deep.equal([
                {frame: 70, notification: Notification.createNext('A')},
                {frame: 110, notification: Notification.createNext('B')},
                {frame: 150, notification: Notification.createComplete()}
            ]);
        });

        it('should parse a marble string, allowing spaces too', () => {
            const result = TestScheduler.parseMarbles('--a--b--|   ', { a: 'A', b: 'B' });
            (<any>expect(result)).to.deep.equal([
                { frame: 20, notification: Notification.createNext('A') },
                { frame: 50, notification: Notification.createNext('B') },
                { frame: 80, notification: Notification.createComplete() }
            ]);
        });

        it('should parse a marble string with a subscription point', () => {
            const result = TestScheduler.parseMarbles('---^---a---b---|', { a: 'A', b: 'B' });
            (<any>expect(result)).to.deep.equal([
                { frame: 40, notification: Notification.createNext('A') },
                { frame: 80, notification: Notification.createNext('B') },
                { frame: 120, notification: Notification.createComplete() }
            ]);
        });

        it('should parse a marble string with an error', () => {
            const result = TestScheduler.parseMarbles('-------a---b---#', { a: 'A', b: 'B' }, 'omg error!');
            (<any>expect(result)).to.deep.equal([
                { frame: 70, notification: Notification.createNext('A') },
                { frame: 110, notification: Notification.createNext('B') },
                { frame: 150, notification: Notification.createError('omg error!') }
            ]);
        });

        it('should default in the letter for the value if no value hash was passed', () => {
            const result = TestScheduler.parseMarbles('--a--b--c--');
            (<any>expect(result)).to.deep.equal([
                { frame: 20, notification: Notification.createNext('a') },
                { frame: 50, notification: Notification.createNext('b') },
                { frame: 80, notification: Notification.createNext('c') },
            ]);
        });

        it('should handle grouped values', () => {
            const result = TestScheduler.parseMarbles('---(abc)---');
            (<any>expect(result)).to.deep.equal([
                { frame: 30, notification: Notification.createNext('a') },
                { frame: 30, notification: Notification.createNext('b') },
                { frame: 30, notification: Notification.createNext('c') }
            ]);
        });
    });


    describe('parseMarblesAsSubscriptions()', () => {
        it('should parse a subscription marble string into a subscriptionLog', () => {
            const result = TestScheduler.parseMarblesAsSubscriptions('---^---!-');
            expect(result.subscribedFrame).to.deep.equal(30);
            expect(result.unsubscribedFrame).to.deep.equal(70);
        });

        it('should parse a subscription marble string with an unsubscription', () => {
            const result = TestScheduler.parseMarblesAsSubscriptions('---^-');
            expect(result.subscribedFrame).to.deep.equal(30);
            expect(result.unsubscribedFrame).to.deep.equal(Number.POSITIVE_INFINITY);
        });

        it('should parse a subscription marble string with a synchronous unsubscription', () => {
            const result = TestScheduler.parseMarblesAsSubscriptions('---(^!)-');
            expect(result.subscribedFrame).to.deep.equal(30);
            expect(result.unsubscribedFrame).to.deep.equal(30);
        });
    });

    describe('createTime()', () => {
        it('should parse a simple time marble string to a number', () => {
            const scheduler = new TestScheduler(null);
            const time = scheduler.createTime('-----|');
            expect(time).to.equal(50);
        });

        it('should throw if not given good marble input', () => {
            const scheduler = new TestScheduler(null);
            expect(() => {
                scheduler.createTime('-a-b-#');
            }).to.throw();
        });
    });

    describe('createColdObservable()', () => {
        it('should create a cold observable', () => {
            const expected = ['A', 'B'];
            const scheduler = new TestScheduler(null);
            const source = scheduler.createColdObservable('--a---b--|', { a: 'A', b: 'B' });
            expect(source instanceof rx.Observable).to.be.true;
            source.subscribe((x: string) => {
                expect(x).to.equal(expected.shift());
            });
            scheduler.flush();
            expect(expected.length).to.equal(0);
        });
    });

    describe('createHotObservable()', () => {
        it('should create a cold observable', () => {
            const expected = ['A', 'B'];
            const scheduler = new TestScheduler(null);
            const source = scheduler.createHotObservable('--a---b--|', { a: 'A', b: 'B' });
            expect(source instanceof rx.Subject).to.be.true;
            source.subscribe((x: string) => {
                expect(x).to.equal(expected.shift());
            });
            scheduler.flush();
            expect(expected.length).to.equal(0);
        });
    });

    describe('scheduler helpers', () => {
        describe('rxTestScheduler', () => {
            it('should exist', () => {
                expect(rxTestScheduler instanceof TestScheduler).to.be.true;
            });
        });

        describe('cold()', () => {
            it('should exist', () => {
                expect(cold).to.exist;
                expect(typeof cold).to.equal('function');
            });

            it('should create a cold observable', () => {
                const expected = [1, 2];
                const source = cold('-a-b-|', { a: 1, b: 2 });
                source.subscribe((x: number) => {
                    expect(x).to.equal(expected.shift());
                }, null, () => {
                    expect(expected.length).to.equal(0);
                });
                expectObservable(source).toBe('-a-b-|', { a: 1, b: 2 });
            });
        });


        describe('hot()', () => {
            it('should exist', () => {
                expect(hot).to.exist;
                expect(typeof hot).to.equal('function');
            });

            it('should create a hot observable', () => {
                const source = hot('---^-a-b-|', { a: 1, b: 2 });
                expect(source instanceof rx.Subject).to.be.true;
                expectObservable(source).toBe('--a-b-|', { a: 1, b: 2 });
            });
        });

        describe('time()', () => {
            it('should exist', () => {
                expect(time).to.exist;
                expect(typeof time).to.equal('function');
            });

            it('should parse a simple time marble string to a number', () => {
                expect(time('-----|')).to.equal(50);
            });
        });

        describe('expectObservable()', () => {
            it('should exist', () => {
                expect(expectObservable).to.exist;
                expect(typeof expectObservable).to.equal('function');
            });

            it('should return an object with a toBe function', () => {
                expect(typeof (expectObservable(ArrayObservable.of(1)).toBe)).to.equal('function');
            });

            it('should append to flushTests array', () => {
                expectObservable(EmptyObservable.create());
                expect((<any>rxTestScheduler).flushTests.length).to.equal(1);
            });

            it('should handle empty', () => {
                expectObservable(EmptyObservable.create()).toBe('|', {});
            });

            it('should handle never', () => {
                expectObservable(EmptyObservable.create()).toBe('-', {});
                expectObservable(EmptyObservable.create()).toBe('---', {});
            });

            it('should accept an unsubscription marble diagram', () => {
                const source = hot('---^-a-b-|');
                const unsubscribe  =  '---!';
                const expected =      '--a';
                expectObservable(source, unsubscribe).toBe(expected);
            });
        });

        describe('expectSubscriptions()', () => {
            it('should exist', () => {
                expect(expectSubscriptions).to.exist;
                expect(typeof expectSubscriptions).to.equal('function');
            });

            it('should return an object with a toBe function', () => {
                expect(typeof (expectSubscriptions([]).toBe)).to.equal('function');
            });

            it('should append to flushTests array', () => {
                expectSubscriptions([]);
                expect((<any>rxTestScheduler).flushTests.length).to.equal(1);
            });

            /* ignored : cold() should return a ColdObservable not an observable
            it('should assert subscriptions of a cold observable', () => {
                const source = cold('---a---b-|');
                const subs =        '^--------!';
                expectSubscriptions(source.subscriptions).toBe(subs);
                source.subscribe();
            });
            */
        });

        describe('end-to-end helper tests', () => {
            /* ignored : cold() should return a ColdObservable not an observable
            it('should be awesome', () => {
                const values = { a: 1, b: 2 };
                const myObservable = cold('---a---b--|', values);
                const subs =              '^---------!';
                expectObservable(myObservable).toBe('---a---b--|', values);
                expectSubscriptions(myObservable.subscriptions).toBe(subs);
            });
             */

            it('should support testing metastreams', () => {
                const x = cold('-a-b|');
                const y = cold('-c-d|');
                const myObservable = hot('---x---y----|', { x: x, y: y });
                const expected =         '---x---y----|';
                const expectedx = cold('-a-b|');
                const expectedy = cold('-c-d|');
                expectObservable(myObservable).toBe(expected, { x: expectedx, y: expectedy });
            });
        });
    });
});
