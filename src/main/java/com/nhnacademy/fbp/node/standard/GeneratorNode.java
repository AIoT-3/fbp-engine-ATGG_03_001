package com.nhnacademy.fbp.node.standard;

import com.nhnacademy.fbp.core.messsage.Message;
import com.nhnacademy.fbp.core.node.AbstractNode;

public class GeneratorNode extends AbstractNode {
    private GeneratorNode(String id) {
        super(id);

        addOutputPort("out");
    }

    public static GeneratorNode create(String id) {
        return new GeneratorNode(id);
    }

    @Override
    protected void onProcess(Message message) {
        // GeneratorNode는 외부에서 메시지를 받지 않으므로 사용하지 않음
    }

    public void generate(String key, Object value) {
        Message message = Message.create()
                .withEntry(key, value);

        send("out", message);
    }
}
