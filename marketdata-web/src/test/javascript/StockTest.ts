import * as rx from 'rxjs/Rx.KitchenSink';
import { expect } from 'chai';
import * as trends from "../../main/javascript/Stock";

declare const rxTestScheduler: rx.TestScheduler;
import {cold, hot, time, expectObservable, expectSubscriptions} from './SchedulerHelper';


describe("Stocks trends engine", () => {

  it("Should parse a quote", () => {
    // given
    const json = "{ \"code\" : \"ibm\" , \"quote\" : 42.34 }"
    // when
    const quote = trends.Quote.parse(json);
    // then
    expect(quote).to.be.not.null;
    expect(quote.code).to.be.equal("ibm");
    expect(quote.quote).to.be.equal(42.34);
  });

  /**
      Test 4
  */
  it("Should parse quotes from the server", () => {
    // given
    const json = "{ \"code\" : \"ibm\" , \"quote\" : 42.34 }"
    const json$ = hot<string>('-q--|', { q: json });
    // when
    const quote$ = trends.parseRawStream(json$);
    // then
    const quote = trends.Quote.parse(json);
    expectObservable(quote$).toBe('-q--|', { q: quote });
  });

  /**
      Test 5
  */
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
  });

  /**
      Test 6
  */
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

  it("Should compute max of last 4 known prices", () => {
    // given
    const json1 = "{ \"code\" : \"ibm\" , \"quote\" : 42.10 }"
    const json2 = "{ \"code\" : \"ibm\" , \"quote\" : 42.20 }"
    const json3 = "{ \"code\" : \"ibm\" , \"quote\" : 42.16 }"
    const json4 = "{ \"code\" : \"ibm\" , \"quote\" : 42.14 }"

    const json$ = hot<string>('-a-b-c-d--', { a: json1, b: json2, c: json3, d: json4 });
    const quote$ = trends.parseRawStream(json$);
    // when
    const trend$ = trends.maxFromPrevious(quote$, 4);
    // then
    expectObservable(trend$).toBe('-------m--', { m: 42.20 });
  })

  it("Should ignore older values when computing max of last 4 known prices", () => {
    // given
    const json0 = "{ \"code\" : \"ibm\" , \"quote\" : 43 }"
    const json1 = "{ \"code\" : \"ibm\" , \"quote\" : 42.10 }"
    const json2 = "{ \"code\" : \"ibm\" , \"quote\" : 42.20 }"
    const json3 = "{ \"code\" : \"ibm\" , \"quote\" : 42.16 }"
    const json4 = "{ \"code\" : \"ibm\" , \"quote\" : 42.14 }"

    const json$ = hot<string>('o-a-b-c-d--', { o: json0, a: json1, b: json2, c: json3, d: json4 });
    const quote$ = trends.parseRawStream(json$);
    // when
    const trend$ = trends.maxFromPrevious(quote$, 4);
    // then
    expectObservable(trend$).toBe('------o-m--', { o: 43, m: 42.20 });
  })

  it("Should compute min of last 4 known prices", () => {
    // given
    const json1 = "{ \"code\" : \"ibm\" , \"quote\" : 42.10 }"
    const json2 = "{ \"code\" : \"ibm\" , \"quote\" : 42.20 }"
    const json3 = "{ \"code\" : \"ibm\" , \"quote\" : 42.16 }"
    const json4 = "{ \"code\" : \"ibm\" , \"quote\" : 42.14 }"

    const json$ = hot<string>('-a-b-c-d--', { a: json1, b: json2, c: json3, d: json4 });
    const quote$ = trends.parseRawStream(json$);
    // when
    const trend$ = trends.minFromPrevious(quote$, 4);
    // then
    expectObservable(trend$).toBe('-------m--', { m: 42.10 });
  })

  it("Should ignore older values when computing min of last 4 known prices", () => {
    // given
    const json0 = "{ \"code\" : \"ibm\" , \"quote\" : 41 }"
    const json1 = "{ \"code\" : \"ibm\" , \"quote\" : 42.10 }"
    const json2 = "{ \"code\" : \"ibm\" , \"quote\" : 42.20 }"
    const json3 = "{ \"code\" : \"ibm\" , \"quote\" : 42.16 }"
    const json4 = "{ \"code\" : \"ibm\" , \"quote\" : 42.14 }"

    const json$ = hot<string>('o-a-b-c-d--', { o: json0, a: json1, b: json2, c: json3, d: json4 });
    const quote$ = trends.parseRawStream(json$);
    // when
    const trend$ = trends.minFromPrevious(quote$, 4);
    // then
    expectObservable(trend$).toBe('------o-m--', { o: 41, m: 42.10 });
  })

});
