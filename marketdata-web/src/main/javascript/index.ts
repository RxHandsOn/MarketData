import * as rx from 'rxjs/Rx';
import {fromEventSource} from './rx-sse';
import LineChart from './LineChart';
import * as stock from './Stock';


window["toggle"]  = function(codes: string[]) : void {
  if (window["cleanUp"]) {
    window["cleanUp"]();
  }

  const code = codes[0];

  const quoteEventSource = new EventSource(`http://localhost:8081?code=${code}`);
  const quoteObservable
    = stock.parseRawStream(
        fromEventSource(quoteEventSource, 'message')
          .pluck<string>('data')
      );

  const lineChart = new LineChart("#spotGraph", `${code} spot`, 1);
  const chartSubscription = quoteObservable.pluck('quote').subscribe(lineChart.getObserver());
  const labelSubscription = stock.detectTrends(quoteObservable).subscribe(q => {
    document.getElementById("currentStock").innerHTML = `Last quote: <span style="background: ${q.color}">${q.quote.quote.toFixed(4)}</span> EUR`
  })

  const minSubscription = stock.minFromPrevious(quoteObservable, 10).subscribe(q => {
    document.getElementById("minStock").innerHTML = `Min: ${q.toFixed(4)} EUR`
  })
  const maxSubscription = stock.maxFromPrevious(quoteObservable, 10).subscribe(q => {
    document.getElementById("maxStock").innerHTML = `Max: ${q.toFixed(4)} EUR`
  })

  const vwapEventSource = new EventSource(`http://localhost:8082?code=${code}`);
  const vwapObservable
    = stock.parseRawVwapStream(
        fromEventSource(vwapEventSource, 'message')
          .pluck<string>('data')
      ).pluck<number>('vwap');

  const vwapLineChart = new LineChart("#vwapGraph", `${code} vwap`, 1);
  const vwapChartSubscription = vwapObservable.subscribe(vwapLineChart.getObserver());
  const vwapLabelSubscription = vwapObservable.subscribe(v => {
    document.getElementById("currentVwap").innerHTML = `Vwap: ${v.toFixed(4)}$`
  })

  window["cleanUp"] = function() : void {
    chartSubscription.unsubscribe();
    labelSubscription.unsubscribe();
    minSubscription.unsubscribe();
    maxSubscription.unsubscribe();
    vwapChartSubscription.unsubscribe();
    vwapLabelSubscription.unsubscribe();
    quoteEventSource.close();
    vwapEventSource.close();
    document.getElementById("currentStock").innerHTML = "";
    document.getElementById("minStock").innerHTML = "";
    document.getElementById("maxStock").innerHTML = "";
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
  document.getElementById("activeStocks").innerHTML += `<a href="#" onclick="toggle(['${st.code}'])">${st.code}</a> - ${st.companyName} - ${st.market}<br/>`
});

stockStaticDataObservable.scan((stockNames, stock) => stockNames.concat([stock.code]), [])
  .subscribe(stocks => {
    const stocksArray = stocks.map(stockName => `'${stockName}'`).join(', ');
    document.getElementById("allStocks").innerHTML = `<a href="#" onclick="toggle([${stocksArray}])">ALL</a><br/>`
  });
