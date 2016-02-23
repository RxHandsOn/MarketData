package com.handson.infra;

import rx.Observable;

public interface Client {

    Observable<String> readServerSideEvents();
}
