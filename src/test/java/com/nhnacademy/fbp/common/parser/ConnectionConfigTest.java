package com.nhnacademy.fbp.common.parser;

import com.nhnacademy.fbp.common.parser.exception.FlowParseException;
import com.nhnacademy.fbp.core.flow.Flow;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class ConnectionConfigTest {

    @Test
    @DisplayName("연결 형식이 잘못된 경우, FlowParseException이 발생한다.")
    void create_WhenInvalidConnection_ThrowsException() {
        // given
        ConnectionConfig config = new ConnectionConfig("in", "out");
        Flow flow = mock(Flow.class);

        // when & then
        assertThatThrownBy(() -> config.connect(flow))
                .isInstanceOf(FlowParseException.class);
    }
}