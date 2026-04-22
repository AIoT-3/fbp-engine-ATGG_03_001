package com.nhnacademy.fbp.infrastructure.http.handler;

import com.nhnacademy.fbp.core.engine.FlowEngine;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class DispatcherHandlerTest {

    @Mock
    private FlowEngine flowEngine;

    @InjectMocks
    private DispatcherHandler dispatcherHandler;

    @Test
    @DisplayName("/flows 엔드포인트로 GET 요청이 들어오면, 컨트롤러를 실행하고 200 OK를 응답한다.")
    void handle_ValidRequest_ReturnsOk() throws Exception {
        // given
        HttpExchange exchange = mock(HttpExchange.class);
        URI uri = new URI("/flows");

        given(exchange.getRequestURI())
                .willReturn(uri);
        given(exchange.getRequestMethod())
                .willReturn("GET");

        Headers headers = new Headers();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        given(exchange.getResponseHeaders())
                .willReturn(headers);
        given(exchange.getResponseBody())
                .willReturn(bos);

        // when
        dispatcherHandler.handle(exchange);

        // then
        assertThat(headers)
                .containsKey("Content-Type")
                .containsEntry("Content-Type", List.of("application/json; charset=UTF-8"));

        then(exchange)
                .should()
                .sendResponseHeaders(eq(200), anyLong());
    }
}