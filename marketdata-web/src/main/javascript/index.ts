import Complex from './Complex'
import * as rx from '@reactivex/rxjs';
import * as d3 from 'd3';
import {fromEventSource} from './rx-sse';

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
    .map((event) => JSON.parse(event.data))
    .pluck('quote');

const width = 960;
const height = 600;
const margins = {
        top: 20,
        bottom: 50,
        left: 70,
        right: 20
    };

const svg = d3.select("svg")
    .attr("width", width)
    .attr("height", height + 200);

const xRange = d3.time.scale().range([margins.left, width - margins.right])
    .domain([new Date(), new Date()]);
const yRange = d3.scale.linear().range([height - margins.bottom, margins.top])
    .domain([0, 0]);
const xAxis = d3.svg.axis()
    .scale(xRange)
    .tickSize(5)
    .tickFormat(d3.time.format("%X"));
const yAxis = d3.svg.axis()
    .scale(yRange)
    .tickSize(5)
    .orient("left");

const xAxisElement = svg.append("g")
    .attr("class", "x axis")
    .attr("transform", "translate(0," + (height - margins.bottom) + ")")
    .call(xAxis);

// Add a label to the middle of the x axis
const xAxisWidth = ((width - margins.right) - margins.left) / 2;
xAxisElement.append("text")
    .attr("x", margins.left + xAxisWidth)
    .attr("y", 0)
    .attr("dy", "3em")
    .style("text-anchor", "middle")
    .text("Time");

const yAxisElement = svg.append("g")
    .attr("class", "y axis")
    .attr("transform", "translate(" + margins.left + ",0)")
    .call(yAxis);

// Add a label to the middle of the y axis
const yAxisHeight = ((height - margins.bottom) - margins.top) / 2;
yAxisElement.append("text")
    .attr("transform", "rotate(-90)")
    .attr("y", 0)
    .attr("x", -(margins.top + yAxisHeight))
    .attr("dy", "-3.5em")
    .style("text-anchor", "middle")
    .text("EUR/USD Quote");

// Define our line series
const lineFunc = d3.svg.line()
    .x(function(d) { return xRange(d.x); })
    .y(function(d) { return yRange(d.y); })
    .interpolate("linear");

svg.append("defs").append("clipPath")
    .attr("id", "clip")
    .append("rect")
    .attr("x", margins.left)
    .attr("y", margins.top)
    .attr("width", width)
    .attr("height", height);

const line = svg.append("g")
    .attr("clip-path", "url(#clip)")
    .append("path")
    .attr("stroke", "blue")
    .attr("fill", "none");

// Add a text element below the chart, which will display the subject of new edits
svg.append("text")
    .attr("class", "edit-text")
    .attr("transform", "translate(" + margins.left + "," + (height + 20)  + ")")
    .attr("width", width - margins.left);

const maxNumberOfDataPoints = 100;

function update(updates) {
    // Update the ranges of the chart to reflect the new data
    if (updates.length > 0)   {
        xRange.domain(d3.extent(updates, function(d) { return d.x; }));
        yRange.domain([d3.min(updates, function(d) { return d.y; }),
            d3.max(updates, function(d) { return d.y; })]);
    }

    // Until we have filled up our data window, we just keep adding data
    // points to the end of the chart.
    if (updates.length < maxNumberOfDataPoints) {
        line.transition()
            .ease("linear")
            .attr("d", lineFunc(updates));

        svg.selectAll("g.x.axis")
            .transition()
            .ease("linear")
            .call(xAxis);
    }
    // Once we have filled up the window, we then remove points from the
    // start of the chart, and move the data over so the chart looks
    // like it is scrolling forwards in time
    else    {
        // Calculate the amount of translation on the x axis which equates to the
        // time between two samples
        var xTranslation = xRange(updates[0].x) - xRange(updates[1].x);

        // Transform our line series immediately, then translate it from
        // right to left. This gives the effect of our chart scrolling
        // forwards in time
        line
            .attr("d", lineFunc(updates))
            .attr("transform", null)
            .transition()
            .duration(200)
            .ease("linear")
            .attr("transform", "translate(" + xTranslation + ", 0)");

        svg.selectAll("g.x.axis")
            .transition()
            .duration(200)
            .ease("linear")
            .call(xAxis);
    }

    svg.selectAll("g.y.axis")
        .transition()
        .call(yAxis);
}

var updatesOverTime = [];

eurUsdQuoteObservable.subscribe((value) => {
    updatesOverTime.push({
        x: new Date(),
        y:(value)
    });
    if (updatesOverTime.length > maxNumberOfDataPoints)  {
        updatesOverTime.shift();
    }
    update(updatesOverTime);
});