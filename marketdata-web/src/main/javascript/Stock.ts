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
  return null;
}

export {
  Quote,
  parseRawStream,
  detectTrends
}
