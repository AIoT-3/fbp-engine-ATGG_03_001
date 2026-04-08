package com.nhnacademy.fbp.node.standard;

import com.nhnacademy.fbp.core.messsage.Message;
import com.nhnacademy.fbp.core.port.InputPort;
import com.nhnacademy.fbp.core.port.OutputPort;
import com.nhnacademy.fbp.core.utils.FbpTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.DOUBLE;
import static org.assertj.core.api.InstanceOfAssertFactories.MAP;

class TemperatureSensorNodeTest {

    @Test
    @DisplayName("메시지를 생성하면, 생성된 메시지의 온도값은 min과 max 사이의 값이 된다.")
    void process_WhenCalled_TemperatureIsBetweenMinAndMax() throws InterruptedException {
        // given
        double min = 0;
        double max = 100;

        TemperatureSensorNode node = TemperatureSensorNode.create("test", min, max);
        OutputPort outputPort = node.getOutputPort("out");
        InputPort nextInputPort = FbpTestUtils.getConnectedInputPort(outputPort);

        // when
        node.process(Message.create());

        // then
        Message found = nextInputPort.take();

        assertThat(found)
                .isNotNull()
                .extracting(m -> m.getPayload("temperature"))
                .isInstanceOf(Double.class)
                .asInstanceOf(DOUBLE)
                .isBetween(min, max);
    }

    @Test
    @DisplayName("메시지를 생성하면, 생성된 메시지에 sensor, temperature, unit, timestamp 키가 모두 존재한다.")
    void process_WhenCalled_ContainsRequiredKeys() throws InterruptedException {
        // given
        TemperatureSensorNode node = TemperatureSensorNode.create("test", 0, 100);
        OutputPort outputPort = node.getOutputPort("out");
        InputPort nextInputPort = FbpTestUtils.getConnectedInputPort(outputPort);

        // when
        node.process(Message.create());

        // then
        Message found = nextInputPort.take();

        assertThat(found)
                .isNotNull()
                .extracting(Message::getPayload)
                .asInstanceOf(MAP)
                .containsKey("sensorId")
                .containsKey("temperature")
                .containsKey("unit")
                .containsKey("timestamp");
    }

    @Test
    @DisplayName("메시지를 생성하면, 생성된 메시지의 sensorId는 해당 센서 노드의 ID와 일치한다.")
    void process_WhenCalled_SensorIdIsNodeId() throws InterruptedException {
        // given
        TemperatureSensorNode node = TemperatureSensorNode.create("test", 0, 100);
        OutputPort outputPort = node.getOutputPort("out");
        InputPort nextInputPort = FbpTestUtils.getConnectedInputPort(outputPort);

        // when
        node.process(Message.create());

        // then
        Message found = nextInputPort.take();

        assertThat(found)
                .isNotNull()
                .extracting(message -> message.getPayload("sensorId"))
                .isEqualTo(node.getId());
    }
}