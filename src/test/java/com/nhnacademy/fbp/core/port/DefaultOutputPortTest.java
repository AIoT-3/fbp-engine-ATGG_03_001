package com.nhnacademy.fbp.core.port;

import com.nhnacademy.fbp.core.connection.Connection;
import com.nhnacademy.fbp.core.messsage.Message;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class DefaultOutputPortTest {

    @Test
    @DisplayName("send()를 호출하면 연결된 Connection에 메시지가 전달된다.")
    void send_WhenCalled_DeliversToConnectedConnection() {
        // given
        DefaultOutputPort outputPort = DefaultOutputPort.create("test");
        Connection connection = Connection.create("test");

        Message message = Message.create();

        outputPort.connect(connection);

        // when
        outputPort.send(message);

        Message found = connection.poll();

        // then
        assertThat(found)
                .isNotNull()
                .isEqualTo(message);
    }

    @Test
    @DisplayName("send()를 호출하면 연결된 모든 Connection에 메시지가 전달된다.")
    void send_WhenCalled_DeliversToAllConnectedConnection() {
        // given
        DefaultOutputPort outputPort = DefaultOutputPort.create("test");
        Connection connection1 = Connection.create("test");
        Connection connection2 = Connection.create("test");

        Message message = Message.create();

        outputPort.connect(connection1);
        outputPort.connect(connection2);

        // when
        outputPort.send(message);

        Message found1 = connection1.poll();
        Message found2 = connection2.poll();

        // then
        assertSoftly(softly -> {
            softly.assertThat(found1)
                    .isNotNull()
                    .isEqualTo(message);
            softly.assertThat(found2)
                    .isNotNull()
                    .isEqualTo(message);
        });
    }

    @Test
    @DisplayName("Connection이 없어도 send()를 호출하면 예외가 발생하지 않는다.")
    void send_WhenNoConnectedConnection_DoesNotThrow() {
        // given
        DefaultOutputPort outputPort = DefaultOutputPort.create("test");

        Message message = Message.create();

        // when & then
        assertDoesNotThrow(() -> outputPort.send(message));
    }
}