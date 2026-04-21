package com.nhnacademy.fbp.infrastructure.parser;

import com.nhnacademy.fbp.core.parser.NodeFactory;
import com.nhnacademy.fbp.core.flow.Flow;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.Map;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class JsonFlowParserTest {

    @Test
    @DisplayName("JSON 스트림으로부터 Flow 객체를 생성할 수 있다.")
    void parse_WhenValidStream_ReturnsFlow() {
        // given
        NodeFactory nodeFactory = NodeFactory.create(Map.of());
        JsonFlowParser parser = JsonFlowParser.create(nodeFactory);
        InputStream inputStream = getClass().getResourceAsStream("/test-flow.json");

        // when
        Flow flow = parser.parse(inputStream);

        // then
        assertDoesNotThrow(flow::validate);

        assertSoftly(softly -> {
            softly.assertThat(flow.getId())
                .isEqualTo("temperature-monitoring");
            softly.assertThat(flow.getNodes())
                .hasSize(3);
            softly.assertThat(flow.getConnections())
                .hasSize(2);
        });
    }
}