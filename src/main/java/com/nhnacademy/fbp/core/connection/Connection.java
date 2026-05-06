package com.nhnacademy.fbp.core.connection;

import com.nhnacademy.fbp.core.message.Message;
import com.nhnacademy.fbp.core.port.InputPort;
import com.nhnacademy.fbp.core.port.OutputPort;
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

    public static Connection connect(String srcId, OutputPort srcPort, String tgtId, InputPort tgtPort) {
        String connectionId = String.format("%s:%s->%s:%s", srcId, srcPort.getName(), tgtId, tgtPort.getName());

        Connection connection = Connection.create(connectionId);
        srcPort.connect(connection);
        connection.setTarget(tgtPort);

        return connection;
    }

    public void deliver(Message message) {
        target.receive(message);
    }
}
