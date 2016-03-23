import * as rx from 'rxjs/Rx';

/**
 * Ported to RxJs5 from https://gist.github.com/searler/204a3fa4a62df09765c8
 *
 * This method wraps an EventSource as an observable sequence.
 *
 * Generalizes https://github.com/Reactive-Extensions/RxJS-DOM/blob/master/src/eventsource.js
 * by allowing different Observables on the same EventSource, based on the event type.
 *
 * Copyright Microsoft Open Technologies
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0                                                                                              *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 *
 * @param {EventSource} source The event source
 * @param {String} event identifier of the event
 * @param {Observer} [openObserver] An optional observer for the 'open' event for the server side event.
 * @returns {Observable} An observable sequence which represents the data from a server-side event.
 */
 export function fromEventSource(source: sse.IEventSourceStatic, event: string, openObserver:rx.Observer<Event> = undefined):rx.Observable<Event> {

    return rx.Observable.create(function (observer:rx.Observer<Event>) {
        function onOpen(e:Event) {
            openObserver.next(e);
            openObserver.complete();
            source.removeEventListener('open', onOpen, false);
        }

        function onError(e:any) {
            if (e.readyState === EventSource.CLOSED) {
                observer.complete();
            } else {
                observer.error(e);
            }
        }

        function onMessage(e:Event) {
            observer.next(e);
        }

        openObserver && source.addEventListener('open', onOpen, false);
        source.addEventListener('error', onError, false);
        source.addEventListener(event, onMessage, false);

        return function () {
            source.removeEventListener('error', onError, false);
            source.removeEventListener(event, onMessage, false);
        };
    });
}