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
  return rx.Observable.empty<Quote>();
}

function detectTrends(quote$: rx.Observable<Quote>) : rx.Observable<Trend>  {
  return rx.Observable.empty<Trend>();
}


function maxFromPrevious(quote$: rx.Observable<Quote>, nbQuotes : number) : rx.Observable<number>  {
  return rx.Observable.empty<number>();
}

function minFromPrevious(quote$: rx.Observable<Quote>, nbQuotes : number) : rx.Observable<number>  {
  return rx.Observable.empty<number>();
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
