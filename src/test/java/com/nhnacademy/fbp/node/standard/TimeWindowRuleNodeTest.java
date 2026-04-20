package com.nhnacademy.fbp.node.standard;

import com.nhnacademy.fbp.core.messsage.Message;
import com.nhnacademy.fbp.core.port.InputPort;
import com.nhnacademy.fbp.core.port.OutputPort;
import com.nhnacademy.fbp.core.utils.FbpTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TimeWindowRuleNodeTest {

    @Test
    @DisplayName("조건이 일치하는 메시지 수신 시, 윈도우 내 조건 만족 횟수가 임계값 미만이면 pass 포트로 전달된다.")
    void process_WhenWindowSizeUnderTheThreshold_SendsToPassPort() throws InterruptedException {
        // given
        TimeWindowRuleNode node = TimeWindowRuleNode.create("test", "temperature > 30", 60000, 10);
        OutputPort outputPort = node.getOutputPort("pass");
        InputPort nextInputPort = FbpTestUtils.getConnectedInputPort(outputPort);

        Message message = Message.create()
                .withEntry("temperature", 50);

        // when
        node.onProcess(message);

        // then
        assertThat(nextInputPort.getBufferSize())
                .isEqualTo(1);

        Message found = nextInputPort.take();

        assertThat(found)
                .isNotNull()
                .isEqualTo(message);
    }

    @Test
    @DisplayName("조건이 일치하는 메시지 수신 시, 윈도우 내 조건 만족 횟수가 임계값 이상이면 alert 포르토 전달된다.")
    void process_WhenWindowSizeOverTheThreshold_SendsToAlertPort() throws InterruptedException {
        // given
        TimeWindowRuleNode node = TimeWindowRuleNode.create("test", "temperature > 30", 60000, 2);
        OutputPort outputPort = node.getOutputPort("alert");
        InputPort nextInputPort = FbpTestUtils.getConnectedInputPort(outputPort);

        Message message = Message.create()
                .withEntry("temperature", 50);

        // when
        node.onProcess(message);
        node.onProcess(message);
        node.onProcess(message);

        // then
        assertThat(nextInputPort.getBufferSize())
                .isEqualTo(1);

        Message found = nextInputPort.take();

        assertThat(found)
                .isNotNull()
                .isEqualTo(message);
    }

    @Test
    @DisplayName("조건이 일치하는 메시지 수신 시, windowMs 이전의 이벤트는 윈도우에서 제외된다.")
    void process_WhenWindowMsBefore_DeletesToWindow() throws InterruptedException {
        // given
        TimeWindowRuleNode node = TimeWindowRuleNode.create("test", "temperature > 30", 1000, 2);

        Message message1 = Message.create()
                .withEntry("temperature", 50);

        Message message2 = Message.create()
                .withEntry("temperature", 55);

        // when
        node.onProcess(message1);

        Thread.sleep(2000);

        node.onProcess(message2);

        // then
        assertThat(node.getWindowSize())
                .isEqualTo(1);
    }
}