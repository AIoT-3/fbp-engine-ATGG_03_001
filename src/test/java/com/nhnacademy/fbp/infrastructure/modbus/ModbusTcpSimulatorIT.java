package com.nhnacademy.fbp.infrastructure.modbus;

import com.nhnacademy.fbp.infrastructure.modbus.frame.MBAPHeader;
import com.nhnacademy.fbp.infrastructure.modbus.frame.pdu.WriteSingleRequestPdu;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;

import static org.assertj.core.api.Assertions.assertThat;

class ModbusTcpSimulatorIT {
    private ModbusTcpSimulator simulator;
    private ModbusTcpClient client;

    private static final int TEST_PORT = 15020;
    private static final int REGISTER_COUNT = 100;

    @BeforeEach
    void setUp() {
        simulator = ModbusTcpSimulator.create(TEST_PORT, REGISTER_COUNT);
        simulator.start();

        client = ModbusTcpClient.create("localhost", TEST_PORT);
        client.connect();
    }

    @AfterEach
    void tearDown() {
        if (client != null) {
            client.disconnect();
        }

        if (simulator != null) {
            simulator.stop();
        }
    }

    @Test
    @DisplayName("클라이언트가 잘못된 FC 값으로 요청을 하면, ModbusException이 발생한다.")
    void handleClient_WhenReceiveWrongFC_ThrowsException() throws IOException {
        // given
        WriteSingleRequestPdu requestPdu = new WriteSingleRequestPdu(0, 1);
        MBAPHeader requestHeader = MBAPHeader.createRequest(0, requestPdu, 1);
        int functionCode = 0x99;

        try (Socket socket = new Socket("localhost", TEST_PORT);
            DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()))) {
            out.write(requestHeader.encode());
            out.write(functionCode);
            out.write(requestPdu.encode());
            out.flush();

            MBAPHeader.read(in);
            int errorFunctionCode = in.readUnsignedByte();
            int exceptionCode = in.readUnsignedByte();

            assertThat(errorFunctionCode)
                    .isEqualTo(0x99);

            assertThat(exceptionCode)
                    .isEqualTo(0x01);
        }
    }
}