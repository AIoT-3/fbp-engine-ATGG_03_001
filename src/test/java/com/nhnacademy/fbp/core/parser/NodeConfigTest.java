package com.nhnacademy.fbp.core.parser;

import com.nhnacademy.fbp.core.parser.exception.FlowParseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

class NodeConfigTest {

    @Test
    @DisplayName("노드 ID가 없거나 비어있는 경우, FlowParseException이 발생한다.")
    void create_WhenNotExistsOrEmptyNodeId_ThrowsException() {
        assertSoftly(softly -> {
            softly.assertThatThrownBy(() -> new NodeConfig(null, "test", Map.of()))
                    .isInstanceOf(FlowParseException.class);
            softly.assertThatThrownBy(() -> new NodeConfig("", "test", Map.of()))
                    .isInstanceOf(FlowParseException.class);
        });
    }

    @Test
    @DisplayName("노드 타입이 없거나 비어있는 경우, FlowParseException이 발생한다.")
    void create_WhenNotExistsOrEmptyNodeType_ThrowsException() {
        assertSoftly(softly -> {
            softly.assertThatThrownBy(() -> new NodeConfig("test", null, Map.of()))
                    .isInstanceOf(FlowParseException.class);
            softly.assertThatThrownBy(() -> new NodeConfig("test", "", Map.of()))
                    .isInstanceOf(FlowParseException.class);
        });
    }
}