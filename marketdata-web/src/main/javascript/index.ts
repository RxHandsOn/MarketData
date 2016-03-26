import * as rx from 'rxjs/Rx';
import {fromEventSource} from './rx-sse';
import LineChart from './LineChart';

class Dummy {
  constructor(public name : string) {}
}

let obj = new Dummy("hello!");

console.log("Hello " + obj.name);

rx.Observable.of("world").subscribe(s => console.log("Hello " + s));
rx.Observable
  .fromEvent(document, "keypress")
  .pluck("keyCode")
  .subscribe(code => {
    document.getElementById("container").innerHTML = "Code " + code;
  });

const eventSource = new EventSource('http://localhost:8081?code=IBM');
const eurUsdQuoteObservable = fromEventSource(eventSource, 'message')
    .map((event: any) => JSON.parse(event.data))
    .pluck('quote');

const lineChart = new LineChart();
eurUsdQuoteObservable.subscribe(lineChart.getObserver());
