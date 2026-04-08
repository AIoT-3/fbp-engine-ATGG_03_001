package com.nhnacademy.fbp.core.port;

import com.nhnacademy.fbp.core.messsage.Message;
import com.nhnacademy.fbp.core.utils.FbpTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class DefaultOutputPortTest {

    @Test
    @DisplayName("send()를 호출하면 다음 노드의 InputPort로 메시지가 전달된다.")
    void send_WhenCalled_DeliversToConnectedInputPort() throws InterruptedException {
        // given
        DefaultOutputPort outputPort = DefaultOutputPort.create("test");
        InputPort nextInputPort = FbpTestUtils.getConnectedInputPort(outputPort);

        Message message = Message.create();

        // when
        outputPort.send(message);

        Message found = nextInputPort.poll();

        // then
        assertThat(found)
                .isNotNull()
                .isEqualTo(message);
    }

    @Test
    @DisplayName("send()를 호출하면 연결된 노드의 InputPort에 메시지가 전달된다.")
    void send_WhenCalled_DeliversToAllConnectedInputPort() throws InterruptedException {
        // given
        DefaultOutputPort outputPort = DefaultOutputPort.create("test");
        InputPort nextInputPort1 = FbpTestUtils.getConnectedInputPort(outputPort);
        InputPort nextInputPort2 = FbpTestUtils.getConnectedInputPort(outputPort);

        Message message = Message.create();

        // when
        outputPort.send(message);

        Message found1 = nextInputPort1.poll();
        Message found2 = nextInputPort2.poll();

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
    @DisplayName("연결된 InputPort가 존재하지 않아도 send()를 호출하면 예외가 발생하지 않는다.")
    void send_WhenNotExists_DoesNotThrow() {
        // given
        DefaultOutputPort outputPort = DefaultOutputPort.create("test");

        Message message = Message.create();

        // when & then
        assertDoesNotThrow(() -> outputPort.send(message));
    }
}