import * as d3 from 'd3';
import * as rx from '@reactivex/rxjs';

interface Point {
    x: Date;
    y: number;
}

export default class LineChart {
    constructor() {
        this.initChart();
    }

    private svg:d3.Selection<SVGElement>;
    private xRange:d3.time.Scale<number, number>;
    private yRange:d3.scale.Linear<number, number>;
    private xAxis:d3.svg.Axis;
    private yAxis:d3.svg.Axis;
    private maxNumberOfDataPoints = 100;
    private line:any;
    private lineFunc: d3.svg.Line<any>;
    private updatesOverTime:Point[] = [];

    initChart() {
        const width = 960;
        const height = 600;
        const margins = {
            top: 20,
            bottom: 50,
            left: 70,
            right: 20
        };

        this.svg = d3.select("svg")
            .attr("width", width)
            .attr("height", height + 200);

        this.xRange = d3.time.scale().range([margins.left, width - margins.right])
            .domain([new Date(), new Date()]);
        this.yRange = d3.scale.linear().range([height - margins.bottom, margins.top])
            .domain([0, 0]);
        this.xAxis = d3.svg.axis()
            .scale(this.xRange)
            .tickSize(5)
            .tickFormat(d3.time.format("%X"));
        this.yAxis = d3.svg.axis()
            .scale(this.yRange)
            .tickSize(5)
            .orient("left");

        const xAxisElement = this.svg.append("g")
            .attr("class", "x axis")
            .attr("transform", "translate(0," + (height - margins.bottom) + ")")
            .call(this.xAxis);

        // Add a label to the middle of the x axis
        const xAxisWidth = ((width - margins.right) - margins.left) / 2;
        xAxisElement.append("text")
            .attr("x", margins.left + xAxisWidth)
            .attr("y", 0)
            .attr("dy", "3em")
            .style("text-anchor", "middle")
            .text("Time");

        const yAxisElement = this.svg.append("g")
            .attr("class", "y axis")
            .attr("transform", "translate(" + margins.left + ",0)")
            .call(this.yAxis);

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
        this.lineFunc = d3.svg.line()
            .x(function(d:any) { return this.xRange(d.x); })
            .y(function(d:any) { return this.yRange(d.y); })
            .interpolate("linear");

        this.svg.append("defs").append("clipPath")
            .attr("id", "clip")
            .append("rect")
            .attr("x", margins.left)
            .attr("y", margins.top)
            .attr("width", width)
            .attr("height", height);

        this.line = this.svg.append("g")
            .attr("clip-path", "url(#clip)")
            .append("path")
            .attr("stroke", "blue")
            .attr("fill", "none");

        // Add a text element below the chart, which will display the subject of new edits
        this.svg.append("text")
            .attr("class", "edit-text")
            .attr("transform", "translate(" + margins.left + "," + (height + 20)  + ")")
            .attr("width", width - margins.left);
    }

    private update(updates:Point[]) {
        // Update the ranges of the chart to reflect the new data
        if (updates.length > 0)   {
            this.xRange.domain(d3.extent(updates, function(d:any) { return d.x; }));
            this.yRange.domain([d3.min(updates, function(d) { return d.y; }),
                d3.max(updates, function(d) { return d.y; })]);
        }

        // Until we have filled up our data window, we just keep adding data
        // points to the end of the chart.
        if (updates.length < this.maxNumberOfDataPoints) {
            this.line.transition()
                .ease("linear")
                .attr("d", this.lineFunc(updates));

            this.svg.selectAll("g.x.axis")
                .transition()
                .ease("linear")
                .call(this.xAxis);
        }
        // Once we have filled up the window, we then remove points from the
        // start of the chart, and move the data over so the chart looks
        // like it is scrolling forwards in time
        else    {
            // Calculate the amount of translation on the x axis which equates to the
            // time between two samples
            var xTranslation = this.xRange(updates[0].x) - this.xRange(updates[1].x);

            // Transform our line series immediately, then translate it from
            // right to left. This gives the effect of our chart scrolling
            // forwards in time
            this.line
                .attr("d", this.lineFunc(updates))
                .attr("transform", null)
                .transition()
                .duration(200)
                .ease("linear")
                .attr("transform", "translate(" + xTranslation + ", 0)");

            this.svg.selectAll("g.x.axis")
                .transition()
                .duration(200)
                .ease("linear")
                .call(this.xAxis);
        }

        this.svg.selectAll("g.y.axis")
            .transition()
            .call(this.yAxis);
    }

    getObserver():((value: number) => void) {
        return (value) => {
            this.updatesOverTime.push({
                x: new Date(),
                y: (value)
            });
            if (this.updatesOverTime.length > this.maxNumberOfDataPoints) {
                this.updatesOverTime.shift();
            }
            this.update(this.updatesOverTime);
        };
    }
}
