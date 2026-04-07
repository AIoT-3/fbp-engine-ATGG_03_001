package com.nhnacademy.fbp.core.node.impl;

import com.nhnacademy.fbp.core.connection.Connection;
import com.nhnacademy.fbp.core.messsage.Message;
import com.nhnacademy.fbp.core.node.AbstractNode;
import com.nhnacademy.fbp.core.port.OutputPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class TimerNodeTest {

    @Test
    @DisplayName("initialize()가 호출되면 OutputPort로 메시지가 전송된다.")
    void initialize_WhenCalled_SendsMessageToOutputPort() {
        // given
        AbstractNode timerNode = TimerNode.create("test", 1000);
        Connection connection = Connection.create("test");
        OutputPort outputPort = timerNode.getOutputPort("out");

        outputPort.connect(connection);

        // when
        timerNode.initialize();

        Message found = connection.poll();

        // then
        assertThat(found)
                .isNotNull();
    }

    @Test
    @DisplayName("메시지가 전송될 때마다 tick이 증가한다.")
    void initialize_WhenSendMessage_IncreaseTick() throws InterruptedException {
        // given
        AbstractNode timerNode = TimerNode.create("test", 1000);

        Connection connection = Connection.create("test");

        OutputPort outputPort = timerNode.getOutputPort("out");

        outputPort.connect(connection);

        // when

        timerNode.initialize();

        Message found1 = connection.poll();
        Thread.sleep(1000);
        Message found2 = connection.poll();

        // then
        assertSoftly(softly -> {
            softly.assertThat(found1)
                    .isNotNull()
                    .extracting(message -> message.getPayload("tick"))
                    .isEqualTo(1);
            softly.assertThat(found2)
                    .isNotNull()
                    .extracting(message -> message.getPayload("tick"))
                    .isEqualTo(2);
        });
    }

    @Test
    @DisplayName("shudown()을 호출하면 더 이상 메시지가 생성되지 않는다.")
    void shutdown_WhenCalled_StopsGeneratingMessages() throws InterruptedException {
        // given
        TimerNode timerNode = TimerNode.create("test", 1000);

        Connection connection = Connection.create("test");

        OutputPort outputPort = timerNode.getOutputPort("out");

        outputPort.connect(connection);

        // when
        timerNode.initialize();

        // 첫 번째 메시지 생성

        Thread.sleep(1000);

        // 두 번째 메시지 생성

        timerNode.shutdown();

        Thread.sleep(3000);

        // then
        assertThat(connection.getBufferSize())
                .isEqualTo(2);
    }

    @Test
    @DisplayName("500ms 주기로 설정하면 2초에 4개의 메시지가 생성된다.")
    void create_when500msInterval_Generates4MessageIn2Seconds() throws InterruptedException {
        // given
        TimerNode timerNode = TimerNode.create("test", 500);

        Connection connection = Connection.create("test");

        OutputPort outputPort = timerNode.getOutputPort("out");

        outputPort.connect(connection);

        // when
        timerNode.initialize();

        Thread.sleep(2000);

        // then
        assertThat(connection.getBufferSize())
                .isGreaterThan(3);
    }
}