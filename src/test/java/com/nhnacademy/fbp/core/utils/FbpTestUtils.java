package com.nhnacademy.fbp.core.utils;

import com.nhnacademy.fbp.core.connection.Connection;
import com.nhnacademy.fbp.core.port.DefaultInputPort;
import com.nhnacademy.fbp.core.port.InputPort;
import com.nhnacademy.fbp.core.port.OutputPort;

public final class FbpTestUtils {
    public static InputPort getConnectedInputPort(OutputPort srcOut) {
        InputPort tgtIn = DefaultInputPort.create("in", null);

        Connection connection = Connection.create("test");

        srcOut.connect(connection);
        connection.setTarget(tgtIn);

        return tgtIn;
    }
}
