package com.nhnacademy.fbp.node.standard;

import com.nhnacademy.fbp.core.message.Message;
import com.nhnacademy.fbp.core.port.InputPort;
import com.nhnacademy.fbp.core.port.OutputPort;
import com.nhnacademy.fbp.core.utils.FbpTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class RuleNodeTest {

    @Test
    @DisplayName("조건을 만족하는 메시지를 수신한 경우, match 포트로 전달된다.")
    void process_WhenMatchCondition_SendToMatchPort() throws InterruptedException {
        // given
        RuleNode node = RuleNode.create("test", "temperature > 30");
        OutputPort outputPort = node.getOutputPort("match");
        InputPort nextInputPort = FbpTestUtils.getConnectedInputPort(outputPort);

        Message message = Message.create()
                .withEntry("temperature", 35);

        // when
        node.process(message);

        // then
        assertThat(nextInputPort.getBufferSize())
                .isEqualTo(1);

        Message found = nextInputPort.take();

        assertThat(found)
                .isNotNull()
                .isEqualTo(message);
    }

    @Test
    @DisplayName("조건을 만족하지 않는 메시지를 수신한 경우, mismatch 포트로 전달된다.")
    void process_WhenMismatchCondition_SendToMismatchPort() throws InterruptedException {
        // given
        RuleNode node = RuleNode.create("test", "temperature > 30");
        OutputPort outputPort = node.getOutputPort("mismatch");
        InputPort nextInputPort = FbpTestUtils.getConnectedInputPort(outputPort);

        Message message = Message.create()
                .withEntry("temperature", 20);

        // when
        node.process(message);

        // then
        assertThat(nextInputPort.getBufferSize())
                .isEqualTo(1);

        Message found = nextInputPort.take();

        assertThat(found)
                .isNotNull()
                .isEqualTo(message);
    }

    @Test
    @DisplayName("조건에 사용되는 필드가 없는 메시지를 수신한 경우, 아무 일도 발생하지 않는다.")
    void process_WhenNotExistsConditionField_DoesNothing() {
        // given
        RuleNode node = RuleNode.create("test", "temperature > 30");

        Message message = Message.create()
                .withEntry("humidity", 20);

        // when & then
        assertDoesNotThrow(() -> node.process(message));
    }

    @Test
    @DisplayName("여러 메시지를 연속으로 처리하는 경우, 각각의 메시지가 올바른 포트로 전달된다.")
    void process_WhenMultipleMessage_SendToMatchPort() throws InterruptedException {
        // given
        RuleNode node = RuleNode.create("test", "temperature > 30");
        OutputPort matchOutputPort = node.getOutputPort("match");
        OutputPort mismatchPort = node.getOutputPort("mismatch");

        InputPort nextMatchInputPort = FbpTestUtils.getConnectedInputPort(matchOutputPort);
        InputPort nextMismatchInputPort = FbpTestUtils.getConnectedInputPort(mismatchPort);

        Message message1 = Message.create()
                .withEntry("temperature", 35);

        Message message2 = Message.create()
                .withEntry("temperature", 40);

        Message message3 = Message.create()
                .withEntry("temperature", 30);

        Message message4 = Message.create()
                .withEntry("temperature", 25);

        // when
        node.process(message1);
        node.process(message2);
        node.process(message3);
        node.process(message4);

        // then
        assertSoftly(softly -> {
            softly.assertThat(nextMatchInputPort.getBufferSize())
                    .isEqualTo(2);
            softly.assertThat(nextMismatchInputPort.getBufferSize())
                    .isEqualTo(2);
        });

        Message found1 = nextMatchInputPort.take();
        Message found2 = nextMatchInputPort.take();
        Message found3 = nextMismatchInputPort.take();
        Message found4 = nextMismatchInputPort.take();

        assertSoftly(softly -> {
            softly.assertThat(found1)
                    .isEqualTo(message1);
            softly.assertThat(found2)
                    .isEqualTo(message2);
            softly.assertThat(found3)
                    .isEqualTo(message3);
            softly.assertThat(found4)
                    .isEqualTo(message4);
        });
    }
}