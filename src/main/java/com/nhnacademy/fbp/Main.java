package com.nhnacademy.fbp;

import com.nhnacademy.fbp.core.engine.FlowEngine;
import com.nhnacademy.fbp.core.flow.Flow;
import com.nhnacademy.fbp.infrastructure.modbus.ModbusTcpSimulator;
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
    private static final FlowEngine engine = FlowEngine.create();

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        initFlow1();
        initFlow2();
        initFlow3();
        initFlow4();
        initFlow5();

        while (true) {
            System.out.print("fbp> ");
            String input = reader.readLine();

            execute(input);

            if (input.equals("exit")) {
                break;
            }
        }
    }

    private static void initFlow1() {
        Flow flow = Flow.create("temperature-monitoring")
                .addNode(TimerNode.create("timer", 1000))
                .addNode(FilterNode.create("filter", "tick", 3))
                .addNode(PrintNode.create("printer"));

        flow.connect("timer", "filter")
                .connect("filter", "printer");

        engine.register(flow);
    }

    private static void initFlow2() {
        Flow flow = Flow.create("temperature-alert")
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

    private static void initFlow3() {
        String id = "mqtt-in";
        Flow flow = Flow.create("mqtt-subscribe-test")
                .addNode(MqttSubscriberNode.create(id, "localhost", 1883, "test"))
                .addNode(LogNode.create("log"));

        flow.connect(id, "log");

        engine.register(flow);
    }

    private static void initFlow4() {
        String id = "mqtt-out";
        Flow flow = Flow.create("mqtt-publish-test")
                .addNode(TimerNode.create("timer", 1000))
                .addNode(HumiditySensorNode.create("humidity", 40, 80))
                .addNode(MqttPublisherNode.create(id, "localhost", 1883, "test"));

        flow.connect("timer", "out", "humidity", "trigger");
        flow.connect("humidity", id);

        engine.register(flow);
    }

    private static void initFlow5() {
        ModbusTcpSimulator simulator = ModbusTcpSimulator.create(5020, 100);
        simulator.start();

        Flow flow = Flow.create("modbus-integrated-test")
                .addNode(TimerNode.create("timer-w", 1000))
                .addNode(TemperatureSensorNode.create("temp-sensor", 20, 30))
                .addNode(ModbusWriterNode.create("modbus-write", "localhost", 5020, 1, 0, "temperature", 10))
                .addNode(TimerNode.create("timer-r", 2000))
                .addNode(ModbusReaderNode.create("modbus-read", "localhost", 5020, 1, 0, 1))
                .addNode(LogNode.create("log"));

        flow.connect("timer-w", "out", "temp-sensor", "trigger")
                .connect("temp-sensor", "out", "modbus-write", "in");

        flow.connect("timer-r", "out", "modbus-read", "trigger")
                .connect("modbus-read", "log");

        engine.register(flow);
    }

    private static void execute(String input) {
        String[] parts = input.split(" ");

        String prompt = parts[0].trim();
        String param = parts.length > 1 ? parts[1].trim() : "";

        switch (prompt) {
            case "list" -> engine.listFlows().forEach(System.out::println);
            case "start" -> engine.startFlow(param);
            case "stop" -> engine.stopFlow(param);
            case "exit" -> engine.shutdown();
            default -> {
                return;
            }
        }
    }
}
