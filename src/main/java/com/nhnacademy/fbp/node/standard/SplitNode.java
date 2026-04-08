package com.nhnacademy.fbp.node.standard;

import com.nhnacademy.fbp.core.messsage.Message;
import com.nhnacademy.fbp.core.node.AbstractNode;

public class SplitNode extends AbstractNode {
    private final String key;
    private final double threshold;

    private SplitNode(String id, String key, double threshold) {
        super(id);
        this.key = key;
        this.threshold = threshold;

        addInputPort("in");
        addOutputPort("match");
        addOutputPort("mismatch");
    }

    public static SplitNode create(String id, String key, double threshold) {
        return new SplitNode(id, key, threshold);
    }

    @Override
    protected void onProcess(Message message) {
        double value = message.getPayload(key);

        if (value >= threshold) {
            send("match", message);
        } else {
            send("mismatch", message);
        }
    }
}
