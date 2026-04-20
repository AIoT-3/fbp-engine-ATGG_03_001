package com.nhnacademy.fbp.infrastructure.modbus;

import com.nhnacademy.fbp.infrastructure.modbus.exception.ModbusConnectException;
import com.nhnacademy.fbp.infrastructure.modbus.frame.*;
import com.nhnacademy.fbp.infrastructure.modbus.frame.pdu.ReadHoldingRequestPdu;
import com.nhnacademy.fbp.infrastructure.modbus.frame.pdu.ReadHoldingResponsePdu;
import com.nhnacademy.fbp.infrastructure.modbus.frame.pdu.WriteSingleRequestPdu;
import com.nhnacademy.fbp.infrastructure.modbus.frame.pdu.WriteSingleResponsePdu;
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
            log.error("Modbus 연결 실패: {}", e.getMessage());
            throw new ModbusConnectException(e);
        }
    }

    public void disconnect() {
        if (socket != null && socket.isConnected() && !socket.isClosed()) {
            try {
                in.close();
                out.close();
                socket.close();
            } catch (IOException e) {
                throw new ModbusConnectException(e);
            }
        }
    }

    public int[] readHoldingRegisters(int unitId, int startAddress, int quantity) {
        ReadHoldingRequestPdu requestPdu = new ReadHoldingRequestPdu(startAddress, quantity);
        MBAPHeader requestHeader = MBAPHeader.createRequest(transactionId++, requestPdu, unitId);

        try {
            out.write(requestHeader.encode());
            out.write(requestPdu.encode());
            out.flush();

            MBAPHeader responseHeader = MBAPHeader.read(in);
            ReadHoldingResponsePdu responsePdu = (ReadHoldingResponsePdu) ModbusPdu.readResponse(in, responseHeader);

            int[] result = new int[quantity];

            DataInputStream dataIn = new DataInputStream(new ByteArrayInputStream(responsePdu.data()));

            for (int i = 0; i < quantity; i++) {
                result[i] = dataIn.readUnsignedShort();
            }

            return result;

        } catch (IOException e) {
            throw new ModbusConnectException(e);
        }

    }

    public void writeSingleRegister(int unitId, int address, int value) {
        WriteSingleRequestPdu requestPdu = new WriteSingleRequestPdu(address, value);
        MBAPHeader requestHeader = MBAPHeader.createRequest(transactionId++, requestPdu, unitId);

        try {
            out.write(requestHeader.encode());
            out.write(requestPdu.encode());
            out.flush();

            MBAPHeader responseHeader = MBAPHeader.read(in);
            ModbusPdu.readResponse(in, responseHeader);
        } catch (IOException e) {
            throw new ModbusConnectException(e);
        }
    }
}
