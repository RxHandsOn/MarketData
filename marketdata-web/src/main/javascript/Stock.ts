import * as rx from 'rxjs/Rx';

class Quote {
  constructor(public code:string, public quote:number) {
  }

  public static parse(json : string) : Quote {
    return JSON.parse(json);
  }
}

function parseRawStream(raw$: rx.Observable<string>) : rx.Observable<Quote>  {
  // Etape 0
  // return rx.Observable.empty<Quote>();
  return raw$.map(Quote.parse);
}

function detectTrends(quote$: rx.Observable<Quote>) : rx.Observable<string>  {
  // Etape 0
  // return rx.Observable.empty<string>();
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
  Quote,
  parseRawStream,
  detectTrends,
  maxFromPrevious,
  minFromPrevious,
}
