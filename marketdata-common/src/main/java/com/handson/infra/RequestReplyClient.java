package com.handson.infra;


import rx.Observable;

public interface RequestReplyClient {

    Observable<String> request(String parameter);
}
