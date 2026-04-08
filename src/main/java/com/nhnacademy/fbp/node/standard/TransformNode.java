package com.nhnacademy.fbp.node.standard;

import com.nhnacademy.fbp.core.messsage.Message;
import com.nhnacademy.fbp.core.node.AbstractNode;

import java.util.function.UnaryOperator;

public class TransformNode extends AbstractNode {
    private final UnaryOperator<Message> transformer;

    private TransformNode(String id, UnaryOperator<Message> transformer) {
        super(id);
        this.transformer = transformer;

        addInputPort("in");
        addOutputPort("out");
    }

    public static TransformNode create(String id, UnaryOperator<Message> transformer) {
        return new TransformNode(id, transformer);
    }

    @Override
    protected void onProcess(Message message) {
        if (transformer == null) return;

        send("out", transformer.apply(message));
    }
}
