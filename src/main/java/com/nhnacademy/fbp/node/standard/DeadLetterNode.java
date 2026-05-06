package com.nhnacademy.fbp.node.standard;

import com.nhnacademy.fbp.core.message.Message;
import com.nhnacademy.fbp.core.node.AbstractNode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class DeadLetterNode extends AbstractNode {
    @Getter
    private final List<Message> messages = new ArrayList<>();

    private DeadLetterNode(String id) {
        super(id);
        addInputPort("in");
    }

    public static DeadLetterNode create(String id) {
        return new DeadLetterNode(id);
    }

    @Override
    protected void onProcess(Message message) {
        log.warn("Dead Letter에 메시지 보관됨: {}", message.getId());
        messages.add(message);
    }
}
