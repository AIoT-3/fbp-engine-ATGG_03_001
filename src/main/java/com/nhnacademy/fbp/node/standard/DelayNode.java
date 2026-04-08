package com.nhnacademy.fbp.node.standard;

import com.nhnacademy.fbp.core.messsage.Message;
import com.nhnacademy.fbp.core.node.AbstractNode;

public class DelayNode extends AbstractNode {
    private final long delayMs;

    private DelayNode(String id, long delayMs) {
        super(id);
        this.delayMs = delayMs;

        addInputPort("in");
        addOutputPort("out");
    }

    public static DelayNode create(String id, long delayMs) {
        return new DelayNode(id, delayMs);
    }

    @Override
    protected void onProcess(Message message) {
        try {
            Thread.sleep(delayMs);

            send("out", message);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
