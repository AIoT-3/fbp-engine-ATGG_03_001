package com.nhnacademy.fbp.node.standard;

import com.nhnacademy.fbp.core.message.Message;
import com.nhnacademy.fbp.core.port.InputPort;
import com.nhnacademy.fbp.core.port.OutputPort;
import com.nhnacademy.fbp.core.utils.FbpTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CounterNodeTest {

    @Test
    @DisplayName("process()가 호출될 때마다 카운터가 증가한다.")
    void process_WhenMessageReceived_IncrementsCounter() {
        // given
        CounterNode counterNode = CounterNode.create("test");

        Message message = Message.create();

        // when
        counterNode.process(message);

        int count = counterNode.getCount();

        // then
        assertThat(count)
                .isEqualTo(1);
    }

    @Test
    @DisplayName("메시지가 처리될 때 페이로드에 'count' 키로 현재 카운터 값이 포함된다.")
    void process_WhenCalled_MessageContainsCurrentCount() throws InterruptedException {
        // given
        CounterNode counterNode = CounterNode.create("test");
        OutputPort outputPort = counterNode.getOutputPort("out");
        InputPort inputPort = FbpTestUtils.getConnectedInputPort(outputPort);

        // when
        for (int i = 0; i < 3; i++) {
            counterNode.process(Message.create());
        }

        // then
        assertThat(inputPort.getBufferSize())
                .isEqualTo(3);

        inputPort.take();
        inputPort.take();

        Message found = inputPort.take();

        assertThat(found)
                .isNotNull()
                .extracting(message -> message.getPayload("count"))
                .isInstanceOf(Integer.class)
                .isEqualTo(3);

    }
}