package com.nhnacademy.fbp.node.standard;

import com.nhnacademy.fbp.core.message.Message;
import com.nhnacademy.fbp.core.port.InputPort;
import com.nhnacademy.fbp.core.port.OutputPort;
import com.nhnacademy.fbp.core.utils.FbpTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.DOUBLE;
import static org.assertj.core.api.InstanceOfAssertFactories.MAP;

class HumiditySensorNodeTest {

    @Test
    @DisplayName("메시지를 생성하면, 생성된 메시지의 습도값은 min과 max 사이의 값이 된다.")
    void process_WhenCalled_HumidityIsBetweenMinAndMax() throws InterruptedException {
        // given
        double min = 80;
        double max = 100;

        HumiditySensorNode node = HumiditySensorNode.create("test", min, max);
        OutputPort outputPort = node.getOutputPort("out");
        InputPort nextInputPort = FbpTestUtils.getConnectedInputPort(outputPort);

        Message trigger = Message.create();

        // when
        node.process(trigger);

        // then
        Message found = nextInputPort.take();

        assertThat(found)
                .isNotNull()
                .extracting(message -> found.getPayload("humidity"))
                .isInstanceOf(Double.class)
                .asInstanceOf(DOUBLE)
                .isBetween(min, max);
    }

    @Test
    @DisplayName("메시지를 생성하면, 생성된 메시지에 sensorId, humidity, unit, timestamp 키가 모두 존재한다.")
    void process_WhenCalled_ContainsRequiredKeys() throws InterruptedException {
        // given
        HumiditySensorNode node = HumiditySensorNode.create("test", 80, 100);
        OutputPort outputPort = node.getOutputPort("out");
        InputPort nextInputPort = FbpTestUtils.getConnectedInputPort(outputPort);

        Message trigger = Message.create();

        // when
        node.process(trigger);

        // then
        Message found = nextInputPort.take();

        assertThat(found)
                .isNotNull()
                .extracting(Message::getPayload)
                .asInstanceOf(MAP)
                .containsKey("sensorId")
                .containsKey("humidity")
                .containsKey("unit")
                .containsKey("timestamp");
    }

    @Test
    @DisplayName("트리거 메시지를 3번 처리하면, 3개의 메시지가 생성된다.")
    void process_WhenMultipleTriggered_CreatesMultipleMessages() {
        // given
        HumiditySensorNode node = HumiditySensorNode.create("test", 80, 100);
        OutputPort outputPort = node.getOutputPort("out");
        InputPort nextInputPort = FbpTestUtils.getConnectedInputPort(outputPort);

        Message trigger = Message.create();

        // when
        node.process(trigger);
        node.process(trigger);
        node.process(trigger);

        // then
        assertThat(nextInputPort.getBufferSize())
                .isEqualTo(3);
    }
}