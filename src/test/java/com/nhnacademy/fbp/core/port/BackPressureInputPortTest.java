package com.nhnacademy.fbp.core.port;

import com.nhnacademy.fbp.core.message.Message;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BackPressureInputPortTest {

    @Test
    @DisplayName("DROP_OLDEST 전략: 버퍼가 가득 차면 가장 오래된 메시지를 버리고 새 메시지를 추가한다.")
    void receive_WhenDropOldest_RemovesHead() throws InterruptedException {
        // given
        BackPressureInputPort port = BackPressureInputPort.create("test", null, 2, BackPressureStrategy.DROP_OLDEST);
        port.receive(Message.create().withEntry("id", 1));
        port.receive(Message.create().withEntry("id", 2));

        // when
        port.receive(Message.create().withEntry("id", 3)); // 1번이 버려져야 함

        // then
        assertThat(port.getBufferSize()).isEqualTo(2);
        assertThat(port.take().getPayload().get("id")).isEqualTo(2);
        assertThat(port.take().getPayload().get("id")).isEqualTo(3);
    }

    @Test
    @DisplayName("DROP_NEWEST 전략: 버퍼가 가득 차면 새 메시지를 무시한다.")
    void receive_WhenDropNewest_IgnoresNew() throws InterruptedException {
        // given
        BackPressureInputPort port = BackPressureInputPort.create("test", null, 2, BackPressureStrategy.DROP_NEWEST);
        port.receive(Message.create().withEntry("id", 1));
        port.receive(Message.create().withEntry("id", 2));

        // when
        port.receive(Message.create().withEntry("id", 3)); // 3번이 무시되어야 함

        // then
        assertThat(port.getBufferSize()).isEqualTo(2);
        assertThat(port.take().getPayload().get("id")).isEqualTo(1);
        assertThat(port.take().getPayload().get("id")).isEqualTo(2);
    }
}
