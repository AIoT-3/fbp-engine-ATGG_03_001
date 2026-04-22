package com.nhnacademy.fbp.core.node;

import com.nhnacademy.fbp.core.engine.MetricCollector;
import com.nhnacademy.fbp.core.engine.NoOpMetricCollector;
import com.nhnacademy.fbp.core.message.Message;
import com.nhnacademy.fbp.core.port.DefaultInputPort;
import com.nhnacademy.fbp.core.port.DefaultOutputPort;
import com.nhnacademy.fbp.core.port.InputPort;
import com.nhnacademy.fbp.core.port.OutputPort;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Getter
public abstract class AbstractNode implements Node, Runnable {
    private final String id;
    private final Map<String, InputPort> inputPorts;
    private final Map<String, OutputPort> outputPorts;
    private MetricCollector metricCollector;

    protected AbstractNode(String id) {
        this.id = id;
        inputPorts = new HashMap<>();
        outputPorts = new HashMap<>();

        metricCollector = NoOpMetricCollector.get();
    }

    protected abstract void onProcess(Message message);

    @Override
    public void process(Message message) {
        log.info("SENSOR-ID: {}, PAYLOAD: {}", id, message);
        try {
            long start = System.currentTimeMillis();
            onProcess(message);
            long end = System.currentTimeMillis();

            long duration = end - start;

            metricCollector.record(id, duration);
        } catch (Exception e) {
            metricCollector.recordError(id);
            throw e;
        }
    }

    protected void addInputPort(String name) {
        InputPort inputPort = DefaultInputPort.create(name, this);

        inputPorts.put(name, inputPort);
    }

    protected void addOutputPort(String name) {
        OutputPort outputPort = DefaultOutputPort.create(name);

        outputPorts.put(name, outputPort);
    }

    public InputPort getInputPort(String name) {
        return inputPorts.get(name);
    }

    public OutputPort getOutputPort(String name) {
        return outputPorts.get(name);
    }

    public void setupMonitoring(MetricCollector metricCollector) {
        this.metricCollector = metricCollector;
    }

    protected Message takeMessage(String inputPortName) throws InterruptedException {
        return inputPorts.get(inputPortName).take();
    }

    protected void send(String portName, Message message) {
        OutputPort target = outputPorts.get(portName);

        if (target != null) {
            target.send(message);
        }
    }

    @Override
    public void run() {
        InputPort in = getInputPort("in");

        if (in != null) {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    Message message = in.take();

                    process(message);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
