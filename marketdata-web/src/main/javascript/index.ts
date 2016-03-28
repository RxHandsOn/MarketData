import * as rx from 'rxjs/Rx';
import {fromEventSource} from './rx-sse';
import LineChart from './LineChart';
import * as stock from './Stock';

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



window["toggle"]  = function(code : string) {
  if (window["cleanUp"]) {
    window["cleanUp"]();
  }

  const quoteEventSource = new EventSource(`http://localhost:8081?code=${code}`);
  const quoteObservable
    = stock.parseRawStream(
        fromEventSource(quoteEventSource, 'message')
          .pluck<string>('data')
      ).pluck('quote');

  const lineChart = new LineChart("#spotGraph", `${code} spot`);
  const chartSubscription = quoteObservable.subscribe(lineChart.getObserver());
  const labelSubscription = quoteObservable.subscribe(q => {
    document.getElementById("currentStock").innerHTML = `Last quote: ${q}`
  })

  const vwapEventSource = new EventSource(`http://localhost:8082?code=${code}`);
  const vwapObservable
    = stock.parseRawVwapStream(
        fromEventSource(vwapEventSource, 'message')
          .pluck<string>('data')
      ).pluck('vwap');

  const vwapLineChart = new LineChart("#vwapGraph", `${code} vwap`);
  const vwapChartSubscription = vwapObservable.subscribe(vwapLineChart.getObserver());

  window["cleanUp"] = function() {
    chartSubscription.unsubscribe();
    labelSubscription.unsubscribe();
    vwapChartSubscription.unsubscribe();
    quoteEventSource.close();
    vwapEventSource.close();
    document.getElementById("currentStock").innerHTML = "";
    document.getElementById("spotGraph").innerHTML = "";
    document.getElementById("vwapGraph").innerHTML = "";

  }

}

const activeStocksEventSource = new EventSource('http://localhost:8083');
const stockStaticDataObservable
  = stock.parseStaticDataRawStream(
      fromEventSource(activeStocksEventSource, 'message')
        .pluck<string>('data')
  );
stockStaticDataObservable.subscribe(st => {
  document.getElementById("activeStocks").innerHTML += `<a href="#" onclick="toggle('${st.code}')">${st.code}</a> - ${st.companyName} - ${st.market}<br/>`
});
