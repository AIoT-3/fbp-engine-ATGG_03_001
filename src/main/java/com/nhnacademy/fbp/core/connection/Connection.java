package com.nhnacademy.fbp.core.connection;

import com.nhnacademy.fbp.core.messsage.Message;
import com.nhnacademy.fbp.core.port.InputPort;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Connection {
    @Getter
    private final String id;

    private final BlockingQueue<Message> buffer;

    @Getter
    @Setter
    private InputPort target;

    public static Connection create(String id) {
        return create(id, 10);
    }

    public static Connection create(String id, int bufferSize) {
        return new Connection(
                id,
                new LinkedBlockingQueue<>(bufferSize),
                null
        );
    }

    public void deliver(Message message) {
        try {
            buffer.put(message);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public Message poll() {
        try {
            return buffer.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return null;
    }

    public int getBufferSize() {
        return buffer.size();
    }
}
