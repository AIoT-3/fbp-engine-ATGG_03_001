package com.nhnacademy.fbp.node.standard;

import com.nhnacademy.fbp.core.messsage.Message;
import com.nhnacademy.fbp.core.node.AbstractNode;

public class ThresholdFilterNode extends AbstractNode {
    private final String fieldName;
    private final double threshold;

    private ThresholdFilterNode(String id, String fieldName, double threshold) {
        super(id);
        this.fieldName = fieldName;
        this.threshold = threshold;

        addInputPort("in");
        addOutputPort("alert");
        addOutputPort("normal");
    }

    public static ThresholdFilterNode create(String id, String fieldName, double threshold) {
        return new ThresholdFilterNode(id, fieldName, threshold);
    }

    @Override
    protected void onProcess(Message message) {
        if (!message.hasKey(fieldName)) {
            return;
        }

        double value = message.getPayload(fieldName);

        if (value > threshold) {
            send("alert", message);
        } else {
            send("normal", message);
        }
    }
}
