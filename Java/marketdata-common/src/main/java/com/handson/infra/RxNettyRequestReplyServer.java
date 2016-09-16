package com.handson.infra;

import io.netty.buffer.ByteBuf;
import io.reactivex.netty.RxNetty;
import io.reactivex.netty.protocol.http.server.HttpServer;


public abstract class RxNettyRequestReplyServer {

    private final int port;

    private final String paramName;

    public RxNettyRequestReplyServer(int port, String paramName) {
        this.port = port;
        this.paramName = paramName;
    }

    public HttpServer<ByteBuf, ByteBuf> createServer() {
        return RxNetty.createHttpServer(port, (request, response) -> {

            HttpRequest httpRequest = new HttpRequest(request.getQueryParameters());
            String content = getResponseContent(httpRequest);
            return response.writeStringAndFlush(content);
        });
    }

    protected abstract String getResponseContent(HttpRequest request);

}
