package com.nhnacademy.fbp.core.port;

import com.nhnacademy.fbp.core.connection.Connection;
import com.nhnacademy.fbp.core.message.Message;

import java.util.List;

public interface OutputPort {
    void send(Message message);
    void connect(Connection connection);
    String getName();
    List<Connection> getConnections();
}
