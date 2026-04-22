package com.nhnacademy.fbp.core.port;

import com.nhnacademy.fbp.core.connection.Connection;
import com.nhnacademy.fbp.core.message.Message;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class DefaultOutputPort implements OutputPort {
    private final String name;
    private final List<Connection> connections;

    public DefaultOutputPort(String name) {
        this.name = name;
        connections = new ArrayList<>();
    }

    public static DefaultOutputPort create(String name) {
        return new DefaultOutputPort(name);
    }

    @Override
    public void send(Message message) {
        connections.forEach(connection -> connection.deliver(message));
    }

    @Override
    public void connect(Connection connection) {
        connections.add(connection);
    }
}
