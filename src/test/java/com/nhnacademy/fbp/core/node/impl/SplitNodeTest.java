package com.nhnacademy.fbp.core.node.impl;

import com.nhnacademy.fbp.core.messsage.Message;
import com.nhnacademy.fbp.core.port.InputPort;
import com.nhnacademy.fbp.core.port.OutputPort;
import com.nhnacademy.fbp.core.utils.FbpTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SplitNodeTest {

    @Test
    @DisplayName("process()가 호출되면 threshold 이상인 메시지는 'match' 포트로, 그렇지 않은 메시지는 'mismatch' 포트로 전달된다.")
    void process_WhenCalled_SplitsMessages() throws InterruptedException {
        // given
        String key = "temperature";
        SplitNode splitNode = SplitNode.create("test", key, 30);
        OutputPort matchOutputPort = splitNode.getOutputPort("match");
        OutputPort mismatchOutputPort = splitNode.getOutputPort("mismatch");
        InputPort matchNextInputPort = FbpTestUtils.getConnectedInputPort(matchOutputPort);
        InputPort mismatchNextInputPort = FbpTestUtils.getConnectedInputPort(mismatchOutputPort);

        double lowTemperature = 10;
        double highTemperature = 40;
        Message message1 = Message.create()
                .withEntry(key, lowTemperature);
        Message message2 = Message.create()
                .withEntry(key, highTemperature);

        // when
        splitNode.process(message1);
        splitNode.process(message2);

        // then
        assertThat(matchNextInputPort.getBufferSize())
                .isEqualTo(1);
        assertThat(mismatchNextInputPort.getBufferSize())
                .isEqualTo(1);

        Message matchFound = matchNextInputPort.take();
        Message mismatchFound = mismatchNextInputPort.take();

        assertThat(matchFound)
                .isNotNull()
                .extracting(message -> message.getPayload(key))
                .isInstanceOf(Double.class)
                .isEqualTo(highTemperature);

        assertThat(mismatchFound)
                .isNotNull()
                .extracting(message -> message.getPayload(key))
                .isInstanceOf(Double.class)
                .isEqualTo(lowTemperature);
    }

    @Test
    @DisplayName("threshold와 동일한 메시지는 'match' 포트로 전달된다.")
    void process_WhenValueEqualsThreshold_SendsToMatchPort() throws InterruptedException {
        // given
        String key = "temperature";
        double threshold = 30;
        SplitNode splitNode = SplitNode.create("test", key, threshold);
        OutputPort matchOutputPort = splitNode.getOutputPort("match");
        InputPort matchNextInputPort = FbpTestUtils.getConnectedInputPort(matchOutputPort);

        Message message = Message.create()
                .withEntry(key, threshold);

        // when
        splitNode.process(message);

        // then
        assertThat(matchNextInputPort.getBufferSize())
                .isEqualTo(1);

        Message found = matchNextInputPort.take();

        assertThat(found)
                .isNotNull()
                .extracting(m -> m.getPayload(key))
                .isInstanceOf(Double.class)
                .isEqualTo(threshold);
    }
}