package com.nhnacademy.fbp.core.port.impl;

import com.nhnacademy.fbp.core.messsage.Message;
import com.nhnacademy.fbp.core.node.AbstractNode;
import com.nhnacademy.fbp.core.port.InputPort;
import lombok.Getter;

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
        return create(name, owner, 10);
    }

    public static DefaultInputPort create(String name, AbstractNode owner, int bufferSize) {
        return new DefaultInputPort(name, owner, bufferSize);
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
    public Message take() throws InterruptedException {
        return buffer.take();
    }

    @Override
    public int getBufferSize() {
        return buffer.size();
    }
}
