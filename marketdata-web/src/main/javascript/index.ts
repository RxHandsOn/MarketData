import Complex from './Complex'
import * as rx from '@reactivex/rxjs';

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