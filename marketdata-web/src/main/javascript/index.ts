import Complex from './Complex'
import * as rx from '@reactivex/rxjs';
import * as d3 from 'd3';
import {fromEventSource} from './rx-sse';
import LineChart from './LineChart';

class Dummy {
  constructor(public name : string) {}
}

let obj = new Dummy("hello!");

console.log("Hello " + obj.name);

let cpx = new Complex(1,1);

console.log("Module works! " + cpx.real);

rx.Observable.of("world").subscribe(s => console.log("Hello " + s));
rx.Observable
  .fromEvent(document, "keypress")
  .pluck("keyCode")
  .subscribe(code => {
    document.getElementById("container").innerHTML = "Code " + code;
  });

const eventSource = new EventSource('http://localhost:8096');
const eurUsdQuoteObservable = fromEventSource(eventSource, 'message')
    .map((event: any) => JSON.parse(event.data))
    .pluck('quote');

const lineChart = new LineChart();
eurUsdQuoteObservable.subscribe(lineChart.getObserver());
