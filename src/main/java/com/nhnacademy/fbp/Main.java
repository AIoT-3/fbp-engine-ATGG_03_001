package com.nhnacademy.fbp;

import com.nhnacademy.fbp.core.engine.FlowEngine;
import com.nhnacademy.fbp.core.flow.Flow;
import com.nhnacademy.fbp.node.mqtt.MqttSubscriberNode;
import com.nhnacademy.fbp.node.standard.*;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.mqttv5.common.MqttException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

@Slf4j
public class Main {
    private static final FlowEngine engine = FlowEngine.create();

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        initFlow1();
        initFlow2();
        initFlow3();

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
        try {
            Flow flow = Flow.create("mqtt-test")
                    .addNode(MqttSubscriberNode.create("mqtt", "tcp://localhost:1883", "sensor"))
                    .addNode(LogNode.create("log"));

            flow.connect("mqtt", "log");

            engine.register(flow);
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
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
