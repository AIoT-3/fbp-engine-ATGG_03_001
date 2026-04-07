package com.nhnacademy.fbp.core.node.impl;

import com.nhnacademy.fbp.core.messsage.Message;
import com.nhnacademy.fbp.core.node.AbstractNode;

public class FilterNode extends AbstractNode {
    private final String key;
    private final double threshold;

    private FilterNode(String id, String key, double threshold) {
        super(id);
        this.key = key;
        this.threshold = threshold;

        addInputPort("in");
        addOutputPort("out");
    }

    public static FilterNode create(String id, String key, double threshold) {
        return new FilterNode(id, key, threshold);
    }

    @Override
    protected void onProcess(Message message) {
        Number value = message.getPayload(key);

        if (value != null && value.doubleValue() > threshold) {
            send("out", message);
        }
    }
}
