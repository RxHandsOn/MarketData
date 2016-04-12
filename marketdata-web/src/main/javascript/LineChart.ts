import * as d3 from 'd3';
import * as rx from 'rxjs/Rx';

interface Point {
    x: Date;
    y: number;
}

export default class LineChart {
    constructor(private svgSelector: string,
                private title: string,
                private chartsCount: number) {
        this.initChart();
        for(var i = 0; i < chartsCount; i++) {
            this.updatesOverTime.push([]);
        }
    }

    private svg:d3.Selection<SVGElement>;
    private xRange:d3.time.Scale<number, number>;
    private yRange:d3.scale.Linear<number, number>;
    private xAxis:d3.svg.Axis;
    private yAxis:d3.svg.Axis;
    private lines:any[] = [];
    private lineFuncs: d3.svg.Line<any>[] = [];
    private updatesOverTime:Point[][] = [];

    initChart() {
        const width = 480;
        const height = 300;
        const margins = {
            top: 20,
            bottom: 50,
            left: 70,
            right: 20
        };

        this.svg = d3.select(this.svgSelector)
            .attr("width", width)
            .attr("height", height + 20);

        this.xRange = d3.time.scale().range([margins.left, width - margins.right])
            .domain([new Date(), new Date()]);
        this.yRange = d3.scale.linear().range([height - margins.bottom, margins.top])
            .domain([0, 0]);
        this.xAxis = d3.svg.axis()
            .scale(this.xRange)
            .tickSize(5)
            .ticks(d3.time.minute, 1)
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
            .text(this.title);

        // Define our line series
        for(var i = 0; i < this.chartsCount; i++) {
            this.lineFuncs[i] = d3.svg.line()
                .x((d:any) => { return this.xRange(d.x); })
                .y((d:any) => { return this.yRange(d.y); })
                .interpolate("linear");
        }

        this.svg.append("defs").append("clipPath")
            .attr("id", "clip")
            .append("rect")
            .attr("x", margins.left)
            .attr("y", margins.top)
            .attr("width", width)
            .attr("height", height);

        for(var i = 0; i < this.chartsCount; i++) {
            const color = i === 0 ? 'blue' : 'red';
            this.lines[i] = this.svg.append("g")
                .attr("clip-path", "url(#clip)")
                .append("path")
                .attr("stroke", color)
                .attr("fill", "none");
        }

        // Add a text element below the chart, which will display the subject of new edits
        this.svg.append("text")
            .attr("class", "edit-text")
            .attr("transform", "translate(" + margins.left + "," + (height + 20)  + ")")
            .attr("width", width - margins.left);
    }

    private update(updatesArrays:Point[][]) {
        const updatesConcat = updatesArrays.reduce((a, b) => a.concat(b), []);
        // Update the ranges of the chart to reflect the new data
        if (updatesConcat.length > 0)   {
            this.xRange.domain(d3.extent(updatesConcat, (d:any) => d.x));
            this.yRange.domain(d3.extent(updatesConcat, (d:any) => d.y));
        }

        for(var i = 0; i < this.chartsCount; i++) {
            this.lines[i].transition()
                .ease("linear")
                .attr("d", this.lineFuncs[i](updatesArrays[i]));
        }

        this.svg.selectAll("g.x.axis")
            .transition()
            .ease("linear")
            .call(this.xAxis);


        this.svg.selectAll("g.y.axis")
            .transition()
            .call(this.yAxis);
    }

    getObserver():((value: number) => void) {
        return (value) => {
            this.updatesOverTime[0].push({
                x: new Date(),
                y: (value)
            });
            const FIVE_MINUTES_IN_MS = 5 * 60 * 1000;
            this.updatesOverTime[0] = this.updatesOverTime[0].filter((point: Point) =>
                new Date().getTime() - point.x.getTime() < FIVE_MINUTES_IN_MS);
            window.requestAnimationFrame(() => {
                this.update([this.updatesOverTime[0]]);
            });

        };
    }
}
