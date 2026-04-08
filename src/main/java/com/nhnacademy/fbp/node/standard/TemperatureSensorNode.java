package com.nhnacademy.fbp.node.standard;

import com.nhnacademy.fbp.core.messsage.Message;
import com.nhnacademy.fbp.core.node.AbstractNode;

public class TemperatureSensorNode extends AbstractNode {
    private final double min;
    private final double max;

    private TemperatureSensorNode(String id, double min, double max) {
        super(id);
        this.min = min;
        this.max = max;

        addInputPort("trigger");
        addOutputPort("out");
    }

    public static TemperatureSensorNode create(String id, double min, double max) {
        return new TemperatureSensorNode(id, min, max);
    }

    @Override
    protected void onProcess(Message message) {
        double temperature = min + Math.random() * (max - min);

        String roundedTemp = String.format("%.1f", temperature);

        Message created = Message.create()
                .withEntry("sensorId", getId())
                .withEntry("temperature", Double.parseDouble(roundedTemp))
                .withEntry("unit", "°C")
                .withEntry("timestamp", System.currentTimeMillis());

        send("out", created);
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Message message = takeMessage("trigger");

                process(message);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
