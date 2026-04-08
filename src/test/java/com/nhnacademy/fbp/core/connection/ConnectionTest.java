package com.nhnacademy.fbp.core.connection;

import com.nhnacademy.fbp.core.messsage.Message;
import com.nhnacademy.fbp.core.port.DefaultInputPort;
import com.nhnacademy.fbp.core.port.InputPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConnectionTest {

    @Test
    @DisplayName("deliver()로 전달된 메시지는 연결된 다음 InputPort로 전달된다.")
    void deliver_WhenDeliveredMessage_DeliversConnectedInputPort() throws InterruptedException {
        // given
        Connection connection = Connection.create("test");
        InputPort nextInputPort = DefaultInputPort.create("in", null);

        // when
        connection.setTarget(nextInputPort);

        Message message = Message.create();
        connection.deliver(message);

        Message found = nextInputPort.poll();

        // then
        assertThat(found)
                .isNotNull()
                .isEqualTo(message);
    }

    @Test
    @DisplayName("deliver()로 저장된 메시지는 연결된 다음 InputPort에서 순서대로 꺼낼 수 있다.")
    void deliver_WhenDeliveredAndPoll_ReturnsMessagesInOrder() throws InterruptedException {
        // given
        Connection connection = Connection.create("test");
        InputPort nextInputPort = DefaultInputPort.create("in", null);
        connection.setTarget(nextInputPort);

        Message message1 = Message.create();
        Message message2 = Message.create();
        Message message3 = Message.create();

        connection.deliver(message1);
        connection.deliver(message2);
        connection.deliver(message3);

        // when
        Message found1 = nextInputPort.poll();
        Message found2 = nextInputPort.poll();
        Message found3 = nextInputPort.poll();

        // then
        assertThat(found1)
                .isNotNull()
                .isEqualTo(message1);

        assertThat(found2)
                .isNotNull()
                .isEqualTo(message2);

        assertThat(found3)
                .isNotNull()
                .isEqualTo(message3);
    }
}