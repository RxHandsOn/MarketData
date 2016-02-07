import Complex from './Complex'
import * as rx from 'rx.all'
//import * as DOM from "rx.DOM";

class Dummy {
  constructor(public name : string) {}
}



let obj = new Dummy("hello!");

console.log("Hello " + obj.name);

let cpx = new Complex(1,1);

console.log("Module works! " + cpx.real);

rx.Observable.just("world").subscribe(s => console.log("Hello " + s));
rx.Observable
  .fromEvent(document, "keypress")
  .pluck("keyCode")
  .subscribe(code => {
    document.getElementById("container").innerHTML = "Code " + code;
  });
