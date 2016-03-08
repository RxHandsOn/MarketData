import Complex from './Complex'
import * as rx from '@reactivex/rxjs';
import * as d3 from 'd3';

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
eventSource.addEventListener('message', (e: any) => {
    console.log(`Message from server: ${e.data}`);
});

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
    .tickSubdivide(true)
    .tickFormat(d3.time.format("%X"));
const yAxis = d3.svg.axis()
    .scale(yRange)
    .tickSize(5)
    .orient("left")
    .tickSubdivide(true);

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
    .text("Updates per second");

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

// Add a text element below the chart, which will display the times that new users
// are added
const newUserTextWidth = 150;
svg.append("text")
    .attr("class", "new-user-text")
    .attr("fill", "green")
    .attr("transform", "translate(" + (width - margins.right - newUserTextWidth) + "," + (height + 20)  + ")")
    .attr("width", newUserTextWidth);

const samplingTime = 2000;
const maxNumberOfDataPoints = 20;
