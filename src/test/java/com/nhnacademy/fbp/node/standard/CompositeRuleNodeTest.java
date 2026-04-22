package com.nhnacademy.fbp.node.standard;

import com.nhnacademy.fbp.core.message.Message;
import com.nhnacademy.fbp.core.port.InputPort;
import com.nhnacademy.fbp.core.port.OutputPort;
import com.nhnacademy.fbp.core.utils.FbpTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class CompositeRuleNodeTest {

    @Test
    @DisplayName("AND 노드가 모든 조건이 일치하는 메시지를 수신한 경우, match 포트로 전달된다.")
    void process_WhenAllMatchCondition_SendsToMatchPort() throws InterruptedException {
        // given
        CompositeRuleNode node = CompositeRuleNode.create("test", Operator.AND);
        OutputPort outputPort = node.getOutputPort("match");
        InputPort nextInputPort = FbpTestUtils.getConnectedInputPort(outputPort);

        node.addCondition("temperature > 30");
        node.addCondition("temperature < 40");

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
    @DisplayName("AND 노드가 하나라도 일치하지 않는 조건의 메시지를 수신한 경우, mismatch 포트로 전달된다.")
    void process_WhenOneMismatchCondition_SendsToMismatchPort() throws InterruptedException {
        // given
        CompositeRuleNode node = CompositeRuleNode.create("test", Operator.AND);
        OutputPort outputPort = node.getOutputPort("mismatch");
        InputPort nextInputPort = FbpTestUtils.getConnectedInputPort(outputPort);

        node.addCondition("temperature > 30");
        node.addCondition("temperature < 40");

        Message message = Message.create()
                .withEntry("temperature", 45);

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
    @DisplayName("OR 노드가 하나라도 일치하는 조건의 메시지를 수신한 경우, match 포트로 전달된다.")
    void process_WhenOneMatchCondition_SendsToMatchPort() throws InterruptedException {
        // given
        CompositeRuleNode node = CompositeRuleNode.create("test", Operator.OR);
        OutputPort outputPort = node.getOutputPort("match");
        InputPort nextInputPort = FbpTestUtils.getConnectedInputPort(outputPort);

        node.addCondition("temperature > 30");
        node.addCondition("temperature < 40");

        Message message = Message.create()
                .withEntry("temperature", 45);

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
    @DisplayName("OR 노드가 모든 조건이 일치하지 않는 메시지를 수신한 경우, mismatch 포트로 전달된다.")
    void process_WhenNotExistsAnyMatchCondition_SendsToMismatchPort() throws InterruptedException {
        // given
        CompositeRuleNode node = CompositeRuleNode.create("test", Operator.OR);
        OutputPort outputPort = node.getOutputPort("mismatch");
        InputPort nextInputPort = FbpTestUtils.getConnectedInputPort(outputPort);

        node.addCondition("temperature > 30");
        node.addCondition("temperature < 10");

        Message message = Message.create()
                .withEntry("temperature", 25);

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
        CompositeRuleNode node = CompositeRuleNode.create("test", Operator.OR);

        node.addCondition("temperature > 30");
        node.addCondition("temperature < 10");

        Message message = Message.create()
                .withEntry("humidity", 80);

        // when & then
        assertDoesNotThrow(() -> node.process(message));
    }
}