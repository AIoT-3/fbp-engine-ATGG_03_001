package com.nhnacademy.fbp.core.parser;

import com.nhnacademy.fbp.core.parser.exception.FlowParseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

class FlowConfigTest {

    @Test
    @DisplayName("Flow 아이디가 없거나 비어 있는 경우, FlowParseException이 발생한다.")
    void create_WhenNotExistOrEmptyFlowId_ThrowsException() {
        // when & then
        assertSoftly(softly -> {
            softly.assertThatThrownBy(() -> new FlowConfig(
                            null,
                            "test",
                            null,
                            List.of(new NodeConfig("test", "test", Map.of())),
                            List.of()))
                    .isInstanceOf(FlowParseException.class);
            softly.assertThatThrownBy(() -> new FlowConfig(
                            "",
                            "test",
                            null,
                            List.of(new NodeConfig("test", "test", Map.of())),
                            List.of()))
                    .isInstanceOf(FlowParseException.class);
        });
    }

    @Test
    @DisplayName("Flow 이름이 없거나 비어 있는 경우, FlowParseException이 발생한다.")
    void create_WhenNotExistsOrEmptyFlowName_ThrowsException() {
        // when & then
        assertSoftly(softly -> {
            softly.assertThatThrownBy(() -> new FlowConfig(
                            "test",
                            null,
                            null,
                            List.of(new NodeConfig("test", "test", Map.of())),
                            List.of()))
                    .isInstanceOf(FlowParseException.class);
            softly.assertThatThrownBy(() -> new FlowConfig(
                            "test",
                            "",
                            null,
                            List.of(new NodeConfig("test", "test", Map.of())),
                            List.of()))
                    .isInstanceOf(FlowParseException.class);
        });
    }

    @Test
    @DisplayName("노드가 없거나 비어 있는 경우, FlowParseException이 발생한다.")
    void create_WhenNotExistsOrEmptyNodes_ThrowsException() {
        // when & then
        assertSoftly(softly -> {
            softly.assertThatThrownBy(() -> new FlowConfig(
                            "test",
                            "test",
                            null,
                            null,
                            List.of()))
                    .isInstanceOf(FlowParseException.class);
            softly.assertThatThrownBy(() -> new FlowConfig(
                            "test",
                            "test",
                            null,
                            List.of(),
                            List.of()))
                    .isInstanceOf(FlowParseException.class);
        });
    }
}