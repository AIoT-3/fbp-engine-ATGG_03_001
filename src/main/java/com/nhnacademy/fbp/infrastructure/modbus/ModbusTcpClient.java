package com.nhnacademy.fbp.infrastructure.modbus;

import com.nhnacademy.fbp.infrastructure.modbus.frame.*;
import com.nhnacademy.fbp.infrastructure.modbus.frame.pdu.ReadHoldingRequestPdu;
import com.nhnacademy.fbp.infrastructure.modbus.frame.pdu.ReadHoldingResponsePdu;
import com.nhnacademy.fbp.infrastructure.modbus.frame.pdu.WriteSingleRequestPdu;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;

@Slf4j
public class ModbusTcpClient {
    private final String host;
    private final int port;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private int transactionId;

    private ModbusTcpClient(String host, int port) {
        this.host = host;
        this.port = port;
        transactionId = 0;
    }

    public static ModbusTcpClient create(String host, int port) {
        return new ModbusTcpClient(host, port);
    }

    public void connect() {
        try {
            socket = new Socket(host, port);
            socket.setSoTimeout(3000);
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void disconnect() {
        if (socket != null && socket.isConnected() && !socket.isClosed()) {
            try {
                in.close();
                out.close();
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public int[] readHoldingRegisters(int unitId, int startAddress, int quantity) {
        int length = quantity * 2 + 3;

        MBAPHeader requestHeader = MBAPHeader.createRequest(transactionId++, length, unitId);
        ReadHoldingRequestPdu pdu = new ReadHoldingRequestPdu(startAddress, quantity);

        try {
            out.write(requestHeader.encode());
            out.write(pdu.encode());
            out.flush();

            MBAPHeader responseHeader = MBAPHeader.read(in);

            int responseFunctionCode = in.readUnsignedByte();

            int pduLength = responseHeader.length() - 1;

            ReadHoldingResponsePdu responsePdu = (ReadHoldingResponsePdu) ModbusPdu.readResponse(in, responseFunctionCode, pduLength);

            int[] result = new int[quantity];

            DataInputStream dataIn = new DataInputStream(new ByteArrayInputStream(responsePdu.data()));

            for (int i = 0; i < quantity; i++) {
                result[i] = dataIn.readUnsignedShort();
            }

            return result;

        } catch (IOException e) {
            // TODO: 처리
            throw new RuntimeException(e);
        }

    }



    public void writeSingleRegister(int unitId, int address, int value) {
        MBAPHeader requestHeader = MBAPHeader.createRequest(transactionId++, 1, unitId);
        WriteSingleRequestPdu requestPdu = new WriteSingleRequestPdu(address, value);

        try {
            out.write(requestHeader.encode());
            out.write(requestPdu.encode());
            out.flush();

            MBAPHeader responseHeader = MBAPHeader.read(in);

            int functionCode = in.readUnsignedByte();

            // TODO: 예외 처리

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


}
