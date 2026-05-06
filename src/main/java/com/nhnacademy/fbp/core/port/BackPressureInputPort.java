package com.nhnacademy.fbp.core.port;

import com.nhnacademy.fbp.core.message.Message;
import com.nhnacademy.fbp.core.node.AbstractNode;
import lombok.Setter;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class BackPressureInputPort implements InputPort {
    private final String name;
    private final BlockingQueue<Message> buffer;
    private final AbstractNode owner;

    @Setter
    private BackPressureStrategy strategy;

    private BackPressureInputPort(String name, AbstractNode owner, int bufferSize, BackPressureStrategy strategy) {
        this.name = name;
        this.owner = owner;
        this.strategy = strategy;
        this.buffer = new ArrayBlockingQueue<>(bufferSize);
    }

    public static BackPressureInputPort create(String name, AbstractNode owner, int bufferSize, BackPressureStrategy strategy) {
        return new BackPressureInputPort(name, owner, bufferSize, strategy);
    }

    @Override
    public void receive(Message message) {
        if (buffer.offer(message)) {
            return;
        }

        switch (strategy) {
            case DROP_OLDEST -> {
                buffer.poll();

                try {
                    buffer.put(message);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            case DROP_NEWEST -> {
                // BlockingQueue의 기본 전략
            }
            default -> {
                try {
                    buffer.put(message);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public AbstractNode getOwner() {
        return owner;
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
