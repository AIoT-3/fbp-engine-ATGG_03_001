package com.nhnacademy.fbp.core.port;

import com.nhnacademy.fbp.core.messsage.Message;
import com.nhnacademy.fbp.core.node.AbstractNode;
import com.nhnacademy.fbp.core.node.Node;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Getter
public class DefaultInputPort implements InputPort {
    private final String name;
    private final BlockingQueue<Message> buffer;
    private final AbstractNode owner;

    private DefaultInputPort(String name, AbstractNode owner, int bufferSize) {
        this.name = name;
        this.owner = owner;
        buffer = new ArrayBlockingQueue<>(bufferSize);
    }

    public static DefaultInputPort create(String name, AbstractNode owner) {
        return new DefaultInputPort(name, owner, 10);
    }

    @Override
    public void receive(Message message) {
        try {
            buffer.put(message);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public Message poll() throws InterruptedException {
        return buffer.take();
    }

    @Override
    public int getBufferSize() {
        return buffer.size();
    }
}
