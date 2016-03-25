import * as rx from 'rxjs/Rx.KitchenSink';
import { expect } from 'chai';
import * as trends from "../../main/javascript/Stock";

declare const rxTestScheduler: rx.TestScheduler;
import {cold, hot, time, expectObservable, expectSubscriptions} from './SchedulerHelper';


describe("Trends engine", () => {

  afterEach(() => {
    rxTestScheduler.flush();
  });

  it("Should parse a quote", () => {
    // given
    const json = "{ \"code\" : \"ibm\" , \"quote\" : 42.34 }"
    // when
    const quote = trends.Quote.parse(json);
    // then
    expect(quote).to.be.not.null;
    expect(quote.code).to.be.equal("ibm");
    expect(quote.quote).to.be.equal(42.34);

  }),

  it("Should parse quotes from the server", () => {
    // given
    const json = "{ \"code\" : \"ibm\" , \"quote\" : 42.34 }"
    const json$ = hot<string>('-q--|', { q: json });
    // when
    const quote$ = trends.parseRawStream(json$);
    // then
    const quote = trends.Quote.parse(json);
    expectObservable(quote$).toBe('-q--|', { q: quote });
  }),

  it("Should detect price getting higher", () => {
    // given
    const json1 = "{ \"code\" : \"ibm\" , \"quote\" : 42.34 }"
    const json2 = "{ \"code\" : \"ibm\" , \"quote\" : 42.42 }"
    const json$ = hot<string>('-a-b-|', { a: json1, b: json2 });
    const quote$ = trends.parseRawStream(json$);
    // when
    const trend$ = trends.detectTrends(quote$);
    // then
    expectObservable(trend$).toBe('---s-|', { s: "green" });
  }),

  it("Should detect price getting lower", () => {
    // given
    const json1 = "{ \"code\" : \"ibm\" , \"quote\" : 42.34 }"
    const json2 = "{ \"code\" : \"ibm\" , \"quote\" : 42.12 }"
    const json$ = hot<string>('-a-b-|', { a: json1, b: json2 });
    const quote$ = trends.parseRawStream(json$);
    // when
    const trend$ = trends.detectTrends(quote$);
    // then
    expectObservable(trend$).toBe('---s-|', { s: "red" });
  })
});
