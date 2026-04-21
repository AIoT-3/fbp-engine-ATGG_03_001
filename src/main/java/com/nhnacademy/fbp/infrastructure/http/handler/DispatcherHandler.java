package com.nhnacademy.fbp.infrastructure.http.handler;

import com.nhnacademy.fbp.infrastructure.http.ControllerScanner;
import com.nhnacademy.fbp.infrastructure.http.Route;
import com.nhnacademy.fbp.infrastructure.http.dto.ResponseEntity;
import com.nhnacademy.fbp.infrastructure.http.exception.MethodNotSupportedException;
import com.nhnacademy.fbp.core.engine.FlowEngine;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
public class DispatcherHandler implements HttpHandler {
    private final List<Route> routes;

    public DispatcherHandler(FlowEngine engine) {
        ControllerScanner scanner = new ControllerScanner(engine);
        this.routes = scanner.scan();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        try {
            Route matchRoute = routes.stream()
                    .map(route -> route.execute(path, method))
                    .flatMap(Optional::stream)
                    .findFirst()
                    .orElse(null);

            if (matchRoute == null) {
                ResponseUtils.sendErrorResponse(exchange, 404);
                return;
            }

            Object result = matchRoute.invoke(exchange);

            if (result instanceof ResponseEntity<?> response) {
                ResponseUtils.sendJsonResponse(exchange, response);
            } else {
                ResponseUtils.sendJsonResponse(exchange, ResponseEntity.ok(result));
            }
        } catch (MethodNotSupportedException e) {
            ResponseUtils.sendErrorResponse(exchange, 405);
        } catch (Exception e) {
            log.error("서버 오류 발생: {}", e.getMessage(), e);
            ResponseUtils.sendErrorResponse(exchange, 500);
        }
    }
}
