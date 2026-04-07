package com.nhnacademy.fbp.core.node.impl;

import com.nhnacademy.fbp.core.connection.Connection;
import com.nhnacademy.fbp.core.messsage.Message;
import com.nhnacademy.fbp.core.port.OutputPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SplitNodeTest {

    @Test
    @DisplayName("process()가 호출되면 threshold 이상인 메시지는 'match' 포트로, 그렇지 않은 메시지는 'mismatch' 포트로 전달된다.")
    void process_WhenCalled_SplitsMessages() {
        // given
        String key = "temperature";
        SplitNode splitNode = SplitNode.create("test", key, 30);
        OutputPort matchOutputPort = splitNode.getOutputPort("match");
        OutputPort mismatchOutputPort = splitNode.getOutputPort("mismatch");

        Connection matchConn = Connection.create("test");
        Connection mismatchConn = Connection.create("test");

        matchOutputPort.connect(matchConn);
        mismatchOutputPort.connect(mismatchConn);

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
        assertThat(matchConn.getBufferSize())
                .isEqualTo(1);
        assertThat(mismatchConn.getBufferSize())
                .isEqualTo(1);

        Message matchFound = matchConn.poll();
        Message mismatchFound = mismatchConn.poll();

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
    void process_WhenValueEqualsThreshold_SendsToMatchPort() {
        // given
        String key = "temperature";
        double threshold = 30;
        SplitNode splitNode = SplitNode.create("test", key, threshold);
        OutputPort matchOutputPort = splitNode.getOutputPort("match");
        Connection matchConn = Connection.create("test");

        matchOutputPort.connect(matchConn);

        Message message = Message.create()
                .withEntry(key, threshold);

        // when
        splitNode.process(message);

        // then
        assertThat(matchConn.getBufferSize())
                .isEqualTo(1);

        Message found = matchConn.poll();

        assertThat(found)
                .isNotNull()
                .extracting(m -> m.getPayload(key))
                .isInstanceOf(Double.class)
                .isEqualTo(threshold);
    }
}