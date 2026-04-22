package com.nhnacademy.fbp.node.standard;

import com.nhnacademy.fbp.core.message.Message;
import com.nhnacademy.fbp.core.node.AbstractNode;

public class HumiditySensorNode extends AbstractNode {
    private final double min;
    private final double max;

    private HumiditySensorNode(String id, double min, double max) {
        super(id);
        this.min = min;
        this.max = max;

        addInputPort("trigger");
        addOutputPort("out");
    }

    public static HumiditySensorNode create(String id, double min, double max) {
        return new HumiditySensorNode(id, min, max);
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
                Message message = takeMessage("trigger");

                process(message);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
