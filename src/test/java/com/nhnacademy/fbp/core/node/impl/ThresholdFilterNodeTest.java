package com.nhnacademy.fbp.core.node.impl;

import com.nhnacademy.fbp.core.connection.Connection;
import com.nhnacademy.fbp.core.messsage.Message;
import com.nhnacademy.fbp.core.port.OutputPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class ThresholdFilterNodeTest {

    @Test
    @DisplayName("임계값을 초과하는 메시지를 처리하면, 'alert' 포트로 전달된다.")
    void process_WhenExceedsThreshold_SendToAlertPort() {
        // given
        ThresholdFilterNode node = ThresholdFilterNode.create("test", "temperature", 30);
        OutputPort alertOutputPort = node.getOutputPort("alert");
        Connection connection = Connection.create("test");

        alertOutputPort.connect(connection);

        double temperature = 35.0;
        Message message = Message.create()
                .withEntry("temperature", temperature);

        // when
        node.process(message);

        // then
        assertThat(connection.getBufferSize())
                .isEqualTo(1);

        Message found = connection.poll();

        assertThat(found)
                .isNotNull()
                .isEqualTo(message);
    }

    @Test
    @DisplayName("임계값 이하인 메시지를 처리하면, 'normal' 포트로 전달된다.")
    void process_WhenBelowThreshold_SendToNormalPort() {
        // given
        ThresholdFilterNode node = ThresholdFilterNode.create("test", "temperature", 30);
        OutputPort normalOutputPort = node.getOutputPort("normal");
        Connection connection = Connection.create("test");

        normalOutputPort.connect(connection);

        double temperature = 20;
        Message message = Message.create()
                .withEntry("temperature", temperature);

        // when
        node.process(message);

        // then
        assertThat(connection.getBufferSize())
                .isEqualTo(1);

        Message found = connection.poll();

        assertThat(found)
                .isNotNull()
                .isEqualTo(message);
    }

    @Test
    @DisplayName("임계값과 동일한 메시지를 처리하면, 'normal' 포트로 전달된다.")
    void process_WhenEqualThreshold_SendToNormalPort() {
        // given
        double threshold = 30;
        ThresholdFilterNode node = ThresholdFilterNode.create("test", "temperature", threshold);
        OutputPort normalOutputPort = node.getOutputPort("normal");
        Connection connection = Connection.create("test");

        normalOutputPort.connect(connection);

        Message message = Message.create()
                .withEntry("temperature", threshold);

        // when
        node.process(message);

        // then
        assertThat(connection.getBufferSize())
                .isEqualTo(1);

        Message found = connection.poll();

        assertThat(found)
                .isNotNull()
                .isEqualTo(message);
    }

    @Test
    @DisplayName("대상 키가 없는 메시지를 처리하면 아무런 예외도 발생하지 않고, 메시지가 어느 포트로도 전달되지 않는다.")
    void process_WhenKeyNotExists_DoesNothing() {
        // given
        ThresholdFilterNode node = ThresholdFilterNode.create("test", "temperature", 30);
        OutputPort alertOutputPort = node.getOutputPort("alert");
        OutputPort normalOutputPort = node.getOutputPort("normal");
        Connection alertConnection = Connection.create("test");
        Connection normalConnection = Connection.create("test");

        alertOutputPort.connect(alertConnection);
        normalOutputPort.connect(normalConnection);

        Message message = Message.create()
                .withEntry("humidity", 50);

        // when & then
        assertDoesNotThrow(() -> node.process(message));

        assertThat(alertConnection.getBufferSize())
                .isZero();
        assertThat(normalConnection.getBufferSize())
                .isZero();
    }
}