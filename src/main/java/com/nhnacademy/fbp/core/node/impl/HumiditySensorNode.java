package com.nhnacademy.fbp.core.node.impl;

import com.nhnacademy.fbp.core.messsage.Message;
import com.nhnacademy.fbp.core.node.AbstractNode;

public class HumiditySensorNode extends AbstractNode {
    private final double min;
    private final double max;

    protected HumiditySensorNode(String id, double min, double max) {
        super(id);
        this.min = min;
        this.max = max;

        addInputPort("trigger");
        addOutputPort("out");
    }

    @Override
    protected void onProcess(Message message) {
        double humidity = min + Math.random() * (max - min);

        String roundHumidity = String.format("%.1f", humidity);

        Message created = Message.create()
                .withEntry("sensorId", getId())
                .withEntry("humidity", Double.parseDouble(roundHumidity))
                .withEntry("unit", "%")
                .withEntry("timestamp", System.currentTimeMillis());

        send("out", created);
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Message message = poll("trigger");

                process(message);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
