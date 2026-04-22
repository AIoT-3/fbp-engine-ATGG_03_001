package com.nhnacademy.fbp;

import com.nhnacademy.fbp.core.flow.FlowMemoryRepository;
import com.nhnacademy.fbp.core.flow.FlowService;
import com.nhnacademy.fbp.infrastructure.http.HttpApiServer;
import com.nhnacademy.fbp.core.parser.FlowParser;
import com.nhnacademy.fbp.core.parser.NodeFactory;
import com.nhnacademy.fbp.core.engine.FlowEngine;
import com.nhnacademy.fbp.core.flow.Flow;
import com.nhnacademy.fbp.infrastructure.modbus.ModbusTcpSimulator;
import com.nhnacademy.fbp.infrastructure.parser.JsonFlowParser;
import com.nhnacademy.fbp.core.parser.plugin.PluginLoader;
import com.nhnacademy.fbp.node.modbus.ModbusReaderNode;
import com.nhnacademy.fbp.node.modbus.ModbusWriterNode;
import com.nhnacademy.fbp.node.mqtt.MqttPublisherNode;
import com.nhnacademy.fbp.node.mqtt.MqttSubscriberNode;
import com.nhnacademy.fbp.node.standard.*;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Slf4j
public class Main {
    private static final int MQTT_PORT = 1883;
    private static final int MODBUS_PORT = 5020;
    private static final String HOST_NAME = "localhost";
    private static final PluginLoader pluginLoader = PluginLoader.create("plugin");
    private static final NodeFactory nodeFactory = NodeFactory.create(pluginLoader.getPlugins());
    private static final FlowParser parser = JsonFlowParser.create(nodeFactory);
    private static final FlowService flowService = new FlowService(new FlowMemoryRepository(), parser);
    private static final FlowEngine engine = FlowEngine.create(flowService);

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        init();

        HttpApiServer httpApiServer = new HttpApiServer("localhost", 8080, engine);

        httpApiServer.start();

        while (true) {
            System.out.print("fbp> ");
            String input = reader.readLine();

            execute(input);

            if (input.equals("exit")) {
                break;
            }
        }
    }

    private static void init() {
        ModbusTcpSimulator simulator = ModbusTcpSimulator.create(MODBUS_PORT, 100);
        simulator.start();
        // temperatureMonitoring();
        temperatureAlert();
        mqttIn();
        mqttOut();
        modbusIntegrate();
        mqttRuleMqtt();
        crossProtocol();

        Flow flow = parser.parse("temperature-monitoring.json");
        engine.register(flow);

        Flow externalPlugin = parser.parse("plugin-test.json");
        engine.register(externalPlugin);
    }

    private static void temperatureAlert() {
        Flow flow = Flow.create("temperature-alert", "온도 알림")
                .addNode(TimerNode.create("timer", 1000))
                .addNode(TemperatureSensorNode.create("temperature", 15, 45))
                .addNode(ThresholdFilterNode.create("threshold", "temperature", 30))
                .addNode(AlertNode.create("alert"))
                .addNode(LogNode.create("log"))
                .addNode(FileWriterNode.create("file", "log/temperature_output"));

        flow.connect("timer", "out", "temperature", "trigger")
                .connect("temperature", "threshold")
                .connect("threshold", "alert", "alert", "in")
                .connect("threshold", "normal", "log", "in")
                .connect("log", "file");

        engine.register(flow);
    }

    private static void mqttIn() {
        String id = "mqtt-in";
        Flow flow = Flow.create("mqtt-subscribe-test", "MQTT 구독")
                .addNode(MqttSubscriberNode.create(id, HOST_NAME, MQTT_PORT, "test"))
                .addNode(LogNode.create("log"));

        flow.connect(id, "log");

        engine.register(flow);
    }

    private static void mqttOut() {
        String id = "mqtt-out";
        Flow flow = Flow.create("mqtt-publish-test", "MQTT 발행")
                .addNode(TimerNode.create("timer", 1000))
                .addNode(HumiditySensorNode.create("humidity", 40, 80))
                .addNode(MqttPublisherNode.create(id, HOST_NAME, MQTT_PORT, "test"));

        flow.connect("timer", "out", "humidity", "trigger");
        flow.connect("humidity", id);

        engine.register(flow);
    }

    private static void mqttRuleMqtt() {
        Flow flow = Flow.create("mqtt-rule-mqtt", "MQTT -> Rule -> MQTT")
                .addNode(MqttSubscriberNode.create("mqtt-sub", HOST_NAME, MQTT_PORT, "sensor/temp"))
                .addNode(RuleNode.create("rule", "value > 30"))
                .addNode(MqttPublisherNode.create("mqtt-pub", HOST_NAME, MQTT_PORT, "alert/temp"));

        flow.connect("mqtt-sub", "rule").connect("rule", "match", "mqtt-pub", "in");

        engine.register(flow);
    }

    private static void modbusIntegrate() {
        Flow flow = Flow.create("modbus-integrated-test", "모드버스 통합 테스트")
                .addNode(TimerNode.create("timer-w", 1000))
                .addNode(TemperatureSensorNode.create("temp-sensor", 20, 30))
                .addNode(ModbusWriterNode.create("modbus-write", HOST_NAME, MODBUS_PORT, 0, 0, "temperature", 10))
                .addNode(TimerNode.create("timer-r", 2000))
                .addNode(ModbusReaderNode.create("modbus-read", HOST_NAME, MODBUS_PORT, 0, 0, 1))
                .addNode(LogNode.create("log"));

        flow.connect("timer-w", "out", "temp-sensor", "trigger")
                .connect("temp-sensor", "out", "modbus-write", "in");

        flow.connect("timer-r", "out", "modbus-read", "trigger")
                .connect("modbus-read", "log");

        engine.register(flow);
    }

    private static void crossProtocol() {
        Flow flow = Flow.create("cross-protocol", "Modbus-MQTT 테스트")
                .addNode(MqttSubscriberNode.create("mqtt-sub", HOST_NAME, MQTT_PORT, "sensor/temp"))
                .addNode(RuleNode.create("rule", "temperature > 30"))
                .addNode(ModbusWriterNode.create("modbus-write", HOST_NAME, MODBUS_PORT, 0, 0, "temperature", 10))
                .addNode(TimerNode.create("timer", 1000))
                .addNode(ModbusReaderNode.create("modbus-read", HOST_NAME, MODBUS_PORT, 0, 0, 1))
                .addNode(LogNode.create("log"));

        flow.connect("mqtt-sub", "rule")
                .connect("timer", "out", "modbus-read", "trigger")
                .connect("rule", "match", "modbus-write", "in");

        flow.connect("modbus-read", "log");

        engine.register(flow);
    }

    private static void execute(String input) {
        String[] parts = input.split(" ");

        String prompt = parts[0].trim();
        String param = parts.length > 1 ? parts[1].trim() : "";

        try {
            switch (prompt) {
                case "list" -> engine.listFlows().forEach(System.out::println);
                case "start" -> engine.startFlow(param);
                case "stop" -> engine.stopFlow(param);
                case "exit" -> engine.shutdown();
                default -> {
                    return;
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
