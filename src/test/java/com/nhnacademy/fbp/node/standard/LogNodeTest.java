package com.nhnacademy.fbp.node.standard;

import com.nhnacademy.fbp.core.message.Message;
import com.nhnacademy.fbp.core.port.InputPort;
import com.nhnacademy.fbp.core.port.OutputPort;
import com.nhnacademy.fbp.core.utils.FbpTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LogNodeTest {

    @Test
    @DisplayName("LogNode가 메시지를 처리하면 해당 메시지는 그대로 OutputPort로 전달된다.")
    void process_WhenProcess_LogsMessageAndForwards() throws InterruptedException {
        // given
        LogNode logNode = LogNode.create("test");
        OutputPort outputPort = logNode.getOutputPort("out");
        InputPort inputPort = FbpTestUtils.getConnectedInputPort(outputPort);

        Message message = Message.create();

        // when
        logNode.process(message);

        // then
        Message found = inputPort.take();

        assertThat(found)
                .isNotNull()
                .isEqualTo(message);
    }
}