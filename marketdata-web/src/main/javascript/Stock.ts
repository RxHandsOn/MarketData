import * as rx from 'rxjs/Rx';

class Stock {
  constructor(public code:string, public companyName:string, public market:string) {
  }

  public static parse(json : string) : Stock {
    return JSON.parse(json);
  }
}

class Quote {
  constructor(public code:string, public quote:number) {
  }

  public static parse(json : string) : Quote {
    return JSON.parse(json);
  }
}

class Trend {
  constructor(public quote:Quote, public color:string) {
  }
}

class Vwap {
  constructor(public code:string, public vwap:number, public volume:number) {
  }

  public static parse(json : string) : Vwap {
    return JSON.parse(json);
  }
}

function parseStaticDataRawStream(raw$: rx.Observable<string>) : rx.Observable<Stock>  {
  return raw$.map(Stock.parse);
}

function parseRawVwapStream(raw$: rx.Observable<string>) : rx.Observable<Vwap>  {
  return raw$.map(Vwap.parse);
}

function parseRawStream(raw$: rx.Observable<string>) : rx.Observable<Quote>  {
  // Etape 0
  // return rx.Observable.empty<Quote>();
  return raw$.map(Quote.parse);
}

function detectTrends(quote$: rx.Observable<Quote>) : rx.Observable<Trend>  {
  // Etape 0
  // return rx.Observable.empty<string>();
  return quote$.zip(quote$.skip(1),
    (q1, q2) => {
      let color : string;
      if (q2.quote > q1.quote) {
        color = "green"
      } else {
        color = "red"
      }
       return new Trend(q2, color);
     });
}


function maxFromPrevious(quote$: rx.Observable<Quote>, nbQuotes : number) : rx.Observable<number>  {
  // Etape 0
  // return rx.Observable.empty<number>();
  return quote$
    .windowCount(nbQuotes, 1)
    .flatMap(
      q$ => q$.map(q => q.quote)
              .max()
    )
}

function minFromPrevious(quote$: rx.Observable<Quote>, nbQuotes : number) : rx.Observable<number>  {
  // Etape 0
  // return rx.Observable.empty<number>();
  return quote$
    .windowCount(nbQuotes, 1)
    .flatMap(
      q$ => q$.map(q => q.quote)
              .min()
    )
}

export {
  Stock,
  Quote,
  Vwap,
  Trend,
  parseRawStream,
  parseStaticDataRawStream,
  parseRawVwapStream,
  detectTrends,
  maxFromPrevious,
  minFromPrevious,
}
