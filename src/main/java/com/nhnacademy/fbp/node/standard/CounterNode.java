package com.nhnacademy.fbp.node.standard;

import com.nhnacademy.fbp.core.messsage.Message;
import com.nhnacademy.fbp.core.node.AbstractNode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CounterNode extends AbstractNode {

    @Getter
    private int count;

    private CounterNode(String id) {
        super(id);
        this.count = 0;

        addInputPort("in");
        addOutputPort("out");
    }

    public static CounterNode create(String id) {
        return new CounterNode(id);
    }

    @Override
    protected void onProcess(Message message) {
        send("out", message.withEntry("count", ++count));
    }

    @Override
    public void shutdown() {
        log.info("[{}] 총 처리 메시지: {}}건", getId(), count);
    }
}
