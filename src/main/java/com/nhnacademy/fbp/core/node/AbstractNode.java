package com.nhnacademy.fbp.core.node;

import com.nhnacademy.fbp.core.messsage.Message;
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

    protected AbstractNode(String id) {
        this.id = id;
        inputPorts = new HashMap<>();
        outputPorts = new HashMap<>();
    }

    protected abstract void onProcess(Message message);

    @Override
    public void process(Message message) {
        // log.info("SENSOR-ID: {}, PAYLOAD: {}", id, message);
        onProcess(message);
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
