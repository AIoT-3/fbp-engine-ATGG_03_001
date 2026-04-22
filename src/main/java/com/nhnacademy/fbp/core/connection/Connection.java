package com.nhnacademy.fbp.core.connection;

import com.nhnacademy.fbp.core.message.Message;
import com.nhnacademy.fbp.core.port.InputPort;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Connection {
    @Getter
    private final String id;

    @Getter
    @Setter
    private InputPort target;

    public static Connection create(String id) {
        return new Connection(
                id,
                null
        );
    }

    public void deliver(Message message) {
        target.receive(message);
    }
}
