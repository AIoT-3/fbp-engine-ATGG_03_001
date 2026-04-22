package com.nhnacademy.fbp.node.standard;

import com.nhnacademy.fbp.core.message.Message;
import com.nhnacademy.fbp.core.node.AbstractNode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CollectorNode extends AbstractNode {
    private final List<Message> collected;

    private CollectorNode(String id) {
        super(id);
        collected = new ArrayList<>();

        addInputPort("in");
    }

    public static CollectorNode create(String id) {
        return new CollectorNode(id);
    }

    @Override
    protected void onProcess(Message message) {
        collected.add(message);
    }
}
