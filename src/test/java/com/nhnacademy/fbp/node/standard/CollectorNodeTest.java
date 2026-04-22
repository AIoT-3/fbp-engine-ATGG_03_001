package com.nhnacademy.fbp.node.standard;

import com.nhnacademy.fbp.core.message.Message;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CollectorNodeTest {

    @Test
    @DisplayName("노드가 생성되면, 메시지 리스트는 비어 있다.")
    void create_WhenCreate_ListIsEmpty() {
        // given & when
        CollectorNode node = CollectorNode.create("test");

        // then
        assertThat(node.getCollected())
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("메시지를 처리하면, 메시지 리스트에 메시지가 순서대로 추가된다.")
    void process_WhenMessageReceived_AddsToList() {
        // given
        CollectorNode node = CollectorNode.create("test");

        Message message1 = Message.create()
                .withEntry("1", 1);

        Message message2 = Message.create()
                .withEntry("2", 2);

        Message message3 = Message.create()
                .withEntry("3", 3);

        // when
        node.process(message1);
        node.process(message2);
        node.process(message3);

        // then
        assertThat(node.getCollected())
                .isNotEmpty()
                .hasSize(3)
                .extracting(Message::getPayload)
                .containsExactly(
                        message1.getPayload(),
                        message2.getPayload(),
                        message3.getPayload()
                );
    }
}