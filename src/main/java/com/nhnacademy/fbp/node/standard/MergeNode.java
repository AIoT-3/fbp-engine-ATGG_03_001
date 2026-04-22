package com.nhnacademy.fbp.node.standard;

import com.nhnacademy.fbp.core.message.Message;
import com.nhnacademy.fbp.core.node.AbstractNode;

import java.util.HashMap;
import java.util.Map;

public class MergeNode extends AbstractNode {

    private MergeNode(String id) {
        super(id);

        addInputPort("in-1");
        addInputPort("in-2");
        addOutputPort("out");
    }

    public static MergeNode create(String id) {
        return new MergeNode(id);
    }

    @Override
    protected void onProcess(Message message) {
        send("out", message);
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Message pending1 = takeMessage("in-1");
                Message pending2 = takeMessage("in-2");

                Map<String, Object> mergedPayload = new HashMap<>(pending1.getPayload());

                mergedPayload.putAll(pending2.getPayload());

                Message merged = Message.create(mergedPayload);

                process(merged);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

}
