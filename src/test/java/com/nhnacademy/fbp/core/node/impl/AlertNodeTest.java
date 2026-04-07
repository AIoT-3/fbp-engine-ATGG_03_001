package com.nhnacademy.fbp.core.node.impl;

import com.nhnacademy.fbp.core.messsage.Message;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class AlertNodeTest {

    @Test
    @DisplayName("'sensorId'와 'temperature' 키가 모두 있는 메시지를 처리하면 아무런 예외도 발생하지 않는다.")
    void process_WhenKeysExist_DoesNotThrow() {
        // given
        AlertNode node = AlertNode.create("test");
        Message message = Message.create()
                .withEntry("sensorId", "sensor-1")
                .withEntry("temperature", 35.0);

        // when & then
        assertDoesNotThrow(() -> node.process(message));
    }

    @Test
    @DisplayName("'temperature' 키가 없는 메시지를 처리하면 아무런 예외도 발생하지 않는다.")
    void process_WhenKeyNotExists_DoesNotThrow() {
        // given
        AlertNode node = AlertNode.create("test");

        // when & then
        assertDoesNotThrow(() -> node.process(Message.create()));
    }
}