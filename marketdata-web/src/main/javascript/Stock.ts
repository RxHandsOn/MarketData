import * as rx from 'rxjs/Rx';

class Quote {
  constructor(public code:string, public quote:number) {
  }

  public static parse(json : string) : Quote {
    return JSON.parse(json);
  }
}

function parseRawStream(raw$: rx.Observable<string>) : rx.Observable<Quote>  {
  return raw$.map(Quote.parse);
}

function detectTrends(quote$: rx.Observable<Quote>) : rx.Observable<string>  {
  return quote$.zip(quote$.skip(1),
    (q1, q2) => {
      let result : string;
      if (q2.quote > q1.quote) {
        result = "green"
      } else {
        result = "red"
      }
       return result;
     });
}

export {
  Quote,
  parseRawStream,
  detectTrends
}
