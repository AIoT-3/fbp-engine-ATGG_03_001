package com.nhnacademy.fbp.core.port;

import com.nhnacademy.fbp.core.messsage.Message;
import com.nhnacademy.fbp.core.node.AbstractNode;
import com.nhnacademy.fbp.core.node.Node;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class DefaultInputPort implements InputPort {
    private final String name;
    private final AbstractNode owner;

    public static DefaultInputPort create(String name, AbstractNode owner) {
        return new DefaultInputPort(name, owner);
    }

    @Override
    public void receive(Message message) {
        owner.process(message);
    }
}
