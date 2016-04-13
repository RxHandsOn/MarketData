import * as rx from 'rxjs/Rx';
import {fromEventSource} from './rx-sse';
import LineChart from './LineChart';
import * as stock from './Stock';
import {getColor} from './colors';


window["toggle"]  = function(codes: string[]) : void {
  if (window["cleanUp"]) {
    window["cleanUp"]();
  }

  document.getElementById('quotesTable').style.display = 'table';
  document.getElementById('quotesBody').innerHTML = codes.map((code, index) =>
    `<tr>
      <td style="color: ${getColor(index)}">${code}</td>
      <td id="last-${code}" class="text-xs-right"></td>
      <td id="min-${code}" class="text-xs-right"></td>
      <td id="max-${code}" class="text-xs-right"></td>
      <td id="vwap-${code}" class="text-xs-right"></td>
    </tr>`).join('');

  const spotLineChart = new LineChart("#spotGraph", 'Spot', codes.length);
  const vwapLineChart = new LineChart("#vwapGraph", 'Vwap', codes.length);

  const subscriptions = [];
  const eventSources = [];

  codes.forEach((code, index) => {
    const quoteEventSource = new EventSource(`http://localhost:8081?code=${code}`);
    const quoteObservable
        = stock.parseRawStream(
        fromEventSource(quoteEventSource, 'message')
            .pluck<string>('data')
    );

    subscriptions.push(quoteObservable.pluck('quote').subscribe(spotLineChart.getObserver(index)));
    subscriptions.push(stock.detectTrends(quoteObservable).subscribe(q => {
      const color = q.color === 'green' ? '#5cb85c' : '#d9534f';
      document.getElementById(`last-${code}`).innerHTML = `<span style="background: ${color}">${q.quote.quote.toFixed(4)}</span> EUR`
    }));

    subscriptions.push(stock.minFromPrevious(quoteObservable, 10).subscribe(q => {
      document.getElementById(`min-${code}`).innerHTML = `${q.toFixed(4)} EUR`
    }));
    subscriptions.push(stock.maxFromPrevious(quoteObservable, 10).subscribe(q => {
      document.getElementById(`max-${code}`).innerHTML = `${q.toFixed(4)} EUR`
    }));

    const vwapEventSource = new EventSource(`http://localhost:8082?code=${code}`);
    const vwapObservable
        = stock.parseRawVwapStream(
        fromEventSource(vwapEventSource, 'message')
            .pluck<string>('data')
    ).pluck<number>('vwap');

    subscriptions.push(vwapObservable.subscribe(vwapLineChart.getObserver(index)));
    subscriptions.push(vwapObservable.subscribe(v => {
      document.getElementById(`vwap-${code}`).innerHTML = `${v.toFixed(4)}$`
    }));

    eventSources.push(quoteEventSource, vwapEventSource);
  });


  window["cleanUp"] = () => {
    subscriptions.forEach(_ => _.unsubscribe());
    eventSources.forEach(_ => _.close());
    document.getElementById("spotGraph").innerHTML = "";
    document.getElementById("vwapGraph").innerHTML = "";
    document.getElementById('quotesTable').style.display = 'none';
  }
};

const activeStocksEventSource = new EventSource('http://localhost:8083');
const stockStaticDataObservable
  = stock.parseStaticDataRawStream(
      fromEventSource(activeStocksEventSource, 'message')
        .pluck<string>('data')
  );

stockStaticDataObservable.scan((stockNames, stock) => stockNames.concat([stock]), [])
  .subscribe(stocks => {
    const stockLinks = stocks.map(st =>
      `<div class="col-md-2">
        <a href="#" onclick="toggle(['${st.code}'])">
          ${st.code}
        </a><br/> <small>${st.companyName} - ${st.market}</small>
      </div>`);

    const stocksArray = stocks.map(stock => `'${stock.code}'`).join(', ');
    stockLinks.push(`
      <div class="col-md-2">
        <a href="#" onclick="toggle([${stocksArray}])">All stocks</a><br/>
      </div>
    `);

    document.getElementById("activeStocks").innerHTML = `
    <div class="row">
      ${stockLinks.join('')}
    </div>`;
  }, error => console.log("Stocks connection stopped"));
