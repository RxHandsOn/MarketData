import * as rx from 'rxjs/Rx';
import { expect } from 'chai';
import * as trends from "../../main/javascript/Stock";

describe("Trends engine", () => {

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

  it("Should detect price getting higher", () => {
    // TODO
  }),

  it("Should detect price getting lower", () => {
    // TODO
  })
});
