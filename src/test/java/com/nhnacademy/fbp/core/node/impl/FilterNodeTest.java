package com.nhnacademy.fbp.core.node.impl;

import com.nhnacademy.fbp.core.connection.Connection;
import com.nhnacademy.fbp.core.messsage.Message;
import com.nhnacademy.fbp.core.node.AbstractNode;
import com.nhnacademy.fbp.core.port.InputPort;
import com.nhnacademy.fbp.core.port.OutputPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class FilterNodeTest {

    @Test
    @DisplayName("FilterNode를 생성하면 'in' 이름을 가진 InputPort와 'out' 이름을 가진 OutputPort가 같이 생성된다.")
    void create_WhenCreate_CreatesWithInputPortAndOutputPort() {
        // given & when
        FilterNode filterNode = FilterNode.create("test", "threshold", 10);

        InputPort inputPort = filterNode.getInputPort("in");
        OutputPort outputPort = filterNode.getOutputPort("out");

        // then
        assertSoftly(softly -> {
            softly.assertThat(inputPort)
                    .isNotNull();

            softly.assertThat(outputPort)
                    .isNotNull();
        });
    }


    @Test
    @DisplayName("process()가 호출되면 threshold보다 작은 메시지는 필터링되고, 그렇지 않은 메시지는 통과한다.")
    void process_WhenCalled_FiltersMessages() {
        // given
        AbstractNode filterNode = FilterNode.create("test", "threshold", 10.0);

        Connection connection = Connection.create("test");

        OutputPort outputPort = filterNode.getOutputPort("out");

        outputPort.connect(connection);

        Message message1 = Message.create()
                .withEntry("threshold", 5.0);

        Message message2 = Message.create()
                .withEntry("threshold", 15.0);

        // when
        filterNode.process(message1);
        filterNode.process(message2);

        int receiveCount = connection.getBufferSize();

        // then
        assertThat(receiveCount)
                .isEqualTo(1);

        Message found = connection.poll();

        assertThat(found)
                .isNotNull()
                .isEqualTo(message2);
    }
}