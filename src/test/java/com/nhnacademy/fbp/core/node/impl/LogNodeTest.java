package com.nhnacademy.fbp.core.node.impl;

import com.nhnacademy.fbp.core.connection.Connection;
import com.nhnacademy.fbp.core.messsage.Message;
import com.nhnacademy.fbp.core.port.OutputPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LogNodeTest {

    @Test
    @DisplayName("LogNode가 메시지를 처리하면 해당 메시지는 그대로 OutputPort로 전달된다.")
    void process_WhenProcess_LogsMessageAndForwards() {
        // given
        LogNode logNode = LogNode.create("test");
        OutputPort outputPort = logNode.getOutputPort("out");
        Connection connection = Connection.create("test");

        Message message = Message.create();

        outputPort.connect(connection);

        // when
        logNode.process(message);

        // then
        Message found = connection.poll();

        assertThat(found)
                .isNotNull()
                .isEqualTo(message);
    }

    @Test
    @DisplayName("다른 노드로부터 메시지를 수신하면 해당 메시지는 그대로 다음 노드로 전달된다.")
    void process_WhenReceiveMessage_DeliversNextNode() {
        // given
        LogNode previous = LogNode.create("previous");
        LogNode logNode = LogNode.create("test");
        LogNode next = LogNode.create("next");

        Connection prevConn = Connection.create("test");
        prevConn.setTarget(logNode.getInputPort("in"));

        Connection nextConn = Connection.create("test");
        nextConn.setTarget(next.getInputPort("in"));

        previous.getOutputPort("out").connect(prevConn);
        logNode.getOutputPort("out").connect(nextConn);

        Message message = Message.create();

        // when
        logNode.process(message);

        // then
        Message found = nextConn.poll();

        assertThat(found)
                .isNotNull()
                .isEqualTo(message);
    }
}