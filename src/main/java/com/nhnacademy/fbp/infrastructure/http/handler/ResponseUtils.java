package com.nhnacademy.fbp.infrastructure.http.handler;

import com.nhnacademy.fbp.infrastructure.http.dto.ResponseEntity;
import com.nhnacademy.fbp.common.util.JsonUtils;
import com.sun.net.httpserver.HttpExchange;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ResponseUtils {
    public static void sendJsonResponse(HttpExchange exchange, ResponseEntity<?> response) throws IOException {

        if (response.body() == null) {
            exchange.sendResponseHeaders(response.status(), -1);
            return;
        }

        String json = JsonUtils.get().writeValueAsString(response.body());
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(response.status(), bytes.length);

        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(bytes);
            outputStream.flush();
        }
    }

    public static void sendErrorResponse(HttpExchange exchange, int status) throws IOException {
        exchange.sendResponseHeaders(status, -1);
    }
}
