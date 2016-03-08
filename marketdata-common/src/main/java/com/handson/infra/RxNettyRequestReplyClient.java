package com.handson.infra;


import io.reactivex.netty.RxNetty;
import rx.Observable;

import java.io.IOException;
import java.nio.charset.Charset;

public class RxNettyRequestReplyClient implements RequestReplyClient {

    private final int port;
    private final String paramName;

    public RxNettyRequestReplyClient(int port, String paramName) {
        this.port = port;
        this.paramName = paramName;
    }

    @Override
    public Observable<String> request(String parameter) {
        return RxNetty.createHttpGet("http://localhost:" + port + "?" + paramName + "=" + parameter)
                .flatMap(response
                        -> response.getContent()
                            .<String> map(content -> content.toString(Charset.defaultCharset()))
                );
    }

    public static void main(String[] args) throws IOException {
        Observable<String> fx = new RxNettyRequestReplyClient(8099, "code").request("GOOGL");

        fx.toBlocking().forEach(System.out::println);
    }
}
