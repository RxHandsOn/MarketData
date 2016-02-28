package com.handson.infra;

import rx.Observable;

public interface EventStreamClient {

    Observable<String> readServerSideEvents();
}
