package com.handson.infra;


import io.netty.buffer.ByteBuf;
import io.reactivex.netty.RxNetty;
import io.reactivex.netty.protocol.http.server.HttpServer;
import io.reactivex.netty.protocol.http.server.RequestHandlerWithErrorMapper;
import io.reactivex.netty.protocol.http.server.file.FileErrorResponseMapper;
import io.reactivex.netty.protocol.http.server.file.FileRequestHandler;

import java.io.File;
import java.io.IOException;
import java.net.URI;

public class StaticServer {

    private final String rootDir;
    private final int port;

    public StaticServer(String rootDir, int port) {
        this.rootDir = rootDir;
        this.port = port;
    }

    public HttpServer<ByteBuf, ByteBuf> createServer() {
        try {
            final String ideBasePath = new File(".").getCanonicalPath();
            final File rootDirectory;
            if (ideBasePath.contains(rootDir)) {
                // for eclipse
                rootDirectory = new File(".");
            } else {
                // for intellij
                rootDirectory = new File(rootDir);
            }
            return RxNetty.createHttpServer(port,
                    RequestHandlerWithErrorMapper.from(
                            new LocalDirectoryRequestHandler(rootDirectory),
                            new FileErrorResponseMapper()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static class LocalDirectoryRequestHandler extends FileRequestHandler {

        private final File directory;

        public LocalDirectoryRequestHandler(File directory) {
            this.directory =  directory;
        }

        @Override
        protected URI resolveUri(String path) {
            if (File.separator.equals(path)) {
                path = "index.html";
            }
            File file = new File(directory, path);
            try {
                if (!file.getCanonicalPath().startsWith(directory.getCanonicalPath())) {
                    System.out.println("Forbiden path");
                    return null;
                }
                if (!file.exists()) {
                    System.out.println("file not found " + path);
                    return null;
                }
                return file.toURI();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }
}
