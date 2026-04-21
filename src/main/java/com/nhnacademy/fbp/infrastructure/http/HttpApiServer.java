package com.nhnacademy.fbp.infrastructure.http;

import com.nhnacademy.fbp.infrastructure.http.handler.DispatcherHandler;
import com.nhnacademy.fbp.core.engine.FlowEngine;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class HttpApiServer {
    private final HttpServer httpServer;

    public HttpApiServer(String host, int port, FlowEngine flowEngine) {
        DispatcherHandler dispatcherHandler = new DispatcherHandler(flowEngine);

        try {
            InetSocketAddress address = new InetSocketAddress(host, port);
            httpServer = HttpServer.create(address, 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        httpServer.createContext("/", dispatcherHandler);
        httpServer.setExecutor(Executors.newFixedThreadPool(10));
    }

    public void start() {
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(0);
    }
}
