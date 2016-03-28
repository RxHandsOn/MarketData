import * as rx from 'rxjs/Rx';
import {fromEventSource} from './rx-sse';
import LineChart from './LineChart';
import * as stock from './Stock';


window["toggle"]  = function(code : string) : void {
  if (window["cleanUp"]) {
    window["cleanUp"]();
  }

  const quoteEventSource = new EventSource(`http://localhost:8081?code=${code}`);
  const quoteObservable
    = stock.parseRawStream(
        fromEventSource(quoteEventSource, 'message')
          .pluck<string>('data')
      );

  const lineChart = new LineChart("#spotGraph", `${code} spot`);
  const chartSubscription = quoteObservable.pluck('quote').subscribe(lineChart.getObserver());
  const labelSubscription = stock.detectTrends(quoteObservable).subscribe(q => {
    document.getElementById("currentStock").innerHTML = `Last quote: <span style="color: ${q.color}">${q.quote.quote}</span>EUR`
  })

  const vwapEventSource = new EventSource(`http://localhost:8082?code=${code}`);
  const vwapObservable
    = stock.parseRawVwapStream(
        fromEventSource(vwapEventSource, 'message')
          .pluck<string>('data')
      ).pluck('vwap');

  const vwapLineChart = new LineChart("#vwapGraph", `${code} vwap`);
  const vwapChartSubscription = vwapObservable.subscribe(vwapLineChart.getObserver());
  const vwapLabelSubscription = vwapObservable.subscribe(v => {
    document.getElementById("currentVwap").innerHTML = `Vwap: ${v}$`
  })

  window["cleanUp"] = function() : void {
    chartSubscription.unsubscribe();
    labelSubscription.unsubscribe();
    vwapChartSubscription.unsubscribe();
    vwapLabelSubscription.unsubscribe();
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
