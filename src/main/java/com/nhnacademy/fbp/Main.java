package com.nhnacademy.fbp;

import com.nhnacademy.fbp.core.engine.FlowEngine;
import com.nhnacademy.fbp.core.flow.Flow;
import com.nhnacademy.fbp.core.node.impl.FilterNode;
import com.nhnacademy.fbp.core.node.impl.PrintNode;
import com.nhnacademy.fbp.core.node.impl.TimerNode;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Slf4j
public class Main {
    private static final FlowEngine engine = FlowEngine.create();

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        initEngine();

        while (true) {
            System.out.print("fbp> ");
            String input = reader.readLine();

            execute(input);

            if (input.equals("exit")) {
                break;
            }
        }
    }

    private static void initEngine() {
        Flow flow = Flow.create("temperature-monitoring")
                .addNode(TimerNode.create("timer", 1000))
                .addNode(FilterNode.create("filter", "tick", 3))
                .addNode(PrintNode.create("printer"));

        flow.connect("timer", "filter")
                .connect("filter", "printer");

        engine.register(flow);
    }

    private static void execute(String input) {
        String[] parts = input.split(" ");

        String prompt = parts[0].trim();
        String param = parts.length > 1 ? parts[1].trim() : "";

        switch (prompt) {
            case "list" -> engine.listFlows();
            case "start" -> engine.startFlow(param);
            case "stop" -> engine.stopFlow(param);
            case "exit" -> engine.shutdown();
            default -> {
                return;
            }
        }
    }
}
