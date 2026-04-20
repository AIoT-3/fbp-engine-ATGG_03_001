package com.nhnacademy.fbp.integrate;

import com.nhnacademy.fbp.core.engine.FlowEngine;
import com.nhnacademy.fbp.core.flow.Flow;
import com.nhnacademy.fbp.core.messsage.Message;
import com.nhnacademy.fbp.infrastructure.modbus.ModbusTcpSimulator;
import com.nhnacademy.fbp.node.modbus.ModbusReaderNode;
import com.nhnacademy.fbp.node.modbus.ModbusWriterNode;
import com.nhnacademy.fbp.node.mqtt.MqttPublisherNode;
import com.nhnacademy.fbp.node.mqtt.MqttSubscriberNode;
import com.nhnacademy.fbp.node.standard.CollectorNode;
import com.nhnacademy.fbp.node.standard.RuleNode;
import com.nhnacademy.fbp.node.standard.TemperatureSensorNode;
import com.nhnacademy.fbp.node.standard.TimerNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.DOUBLE;
import static org.awaitility.Awaitility.await;

@Tag("integration")
class MqttModbusIT {

    @Test
    @DisplayName("MQTT로 발행한 메시지가 RuleNode를 통해 올바르게 분기된다.")
    void integrate_WhenPublished_BranchesToRuleNode() {
        // given
        TimerNode timerNode = TimerNode.create("timer", 100);
        TemperatureSensorNode temperatureSensorNode = TemperatureSensorNode.create("temperature", 20, 40);
        MqttPublisherNode mqttPublisherNode = MqttPublisherNode.create("publisher", "localhost", 1883, "test");
        MqttSubscriberNode mqttSubscriberNode = MqttSubscriberNode.create("subscriber", "localhost", 1883, "test");
        RuleNode ruleNode = RuleNode.create("rule", "temperature > 30");
        CollectorNode matchCollectNode = CollectorNode.create("match-collector");
        CollectorNode mismatchCollectorNode = CollectorNode.create("mismatch-collector");

        Flow flow = Flow.create("mqtt-rule")
                .addNode(mqttPublisherNode)
                .addNode(mqttSubscriberNode)
                .addNode(timerNode)
                .addNode(temperatureSensorNode)
                .addNode(ruleNode)
                .addNode(matchCollectNode)
                .addNode(mismatchCollectorNode);

        flow.connect("timer", "out", "temperature", "trigger");
        flow.connect("temperature", "publisher");
        flow.connect("subscriber", "rule");
        flow.connect("rule", "match", "match-collector", "in");
        flow.connect("rule", "mismatch", "mismatch-collector", "in");

        FlowEngine engine = FlowEngine.create();

        engine.register(flow);

        // when
        engine.startFlow("mqtt-rule");

        // then
        await().atMost(5, TimeUnit.SECONDS)
                .ignoreExceptions()
                .untilAsserted(() -> {
                    List<Message> collected = matchCollectNode.getCollected();

                    assertThat(collected)
                            .isNotEmpty()
                            .first()
                            .extracting(message -> message.getPayload("temperature"))
                            .asInstanceOf(DOUBLE)
                            .isGreaterThan(30);
        });

        await().atMost(5, TimeUnit.SECONDS)
                .ignoreExceptions()
                .untilAsserted(() -> {
                    List<Message> collected = mismatchCollectorNode.getCollected();

                    assertThat(collected)
                            .isNotEmpty()
                            .first()
                            .extracting(message -> message.getPayload("temperature"))
                            .asInstanceOf(DOUBLE)
                            .isLessThanOrEqualTo(30);
        });

        engine.stopFlow("mqtt-rule");
    }

    @Test
    @DisplayName("조건을 만족하는 메시지를 수신하면, MODBUS 레지스터에 값이 기록된다.")
    void integrate_WhenMatchCondition_WritesModbusRegister() {
        // given
        ModbusTcpSimulator simulator = ModbusTcpSimulator.create(5020, 10);

        TimerNode timerNode = TimerNode.create("timer", 100);
        TemperatureSensorNode temperatureSensorNode = TemperatureSensorNode.create("temperature", 20, 40);
        ModbusWriterNode modbusWriterNode = ModbusWriterNode.create("modbus-write", "localhost", 5020, 0, 0, "temperature", 10);
        ModbusReaderNode modbusReaderNode = ModbusReaderNode.create("modbus-read", "localhost", 5020, 0, 0, 1);
        CollectorNode collectorNode = CollectorNode.create("collector");

        Flow flow = Flow.create("mqtt-rule")
                .addNode(timerNode)
                .addNode(temperatureSensorNode)
                .addNode(modbusWriterNode)
                .addNode(modbusReaderNode)
                .addNode(collectorNode);

        flow.connect("timer", "out", "temperature", "trigger");
        flow.connect("temperature", "modbus-write");

        flow.connect("modbus-read", "collector");

        FlowEngine engine = FlowEngine.create();

        engine.register(flow);

        // when
        simulator.start();
        engine.startFlow("mqtt-rule");

        // then
        await().atMost(5, TimeUnit.SECONDS)
                .ignoreExceptions()
                .untilAsserted(() -> {
                    assertThat(simulator.getRegister(0))
                            .isNotZero();
                });

        engine.stopFlow("mqtt-rule");
        simulator.stop();
    }
}
