package com.nhnacademy.fbp.node.standard;

import com.nhnacademy.fbp.core.message.Message;
import com.nhnacademy.fbp.core.node.AbstractNode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PrintNode extends AbstractNode {
    private PrintNode(String id) {
        super(id);

        addInputPort("in");
    }

    public static PrintNode create(String id) {
        return new PrintNode(id);
    }

    @Override
    protected void onProcess(Message message) {
        log.info("{}", message);
    }
}
