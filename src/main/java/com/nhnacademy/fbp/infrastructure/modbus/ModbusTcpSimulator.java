package com.nhnacademy.fbp.infrastructure.modbus;

import com.nhnacademy.fbp.infrastructure.modbus.frame.*;
import com.nhnacademy.fbp.infrastructure.modbus.frame.pdu.ReadHoldingRequestPdu;
import com.nhnacademy.fbp.infrastructure.modbus.frame.pdu.ReadHoldingResponsePdu;
import com.nhnacademy.fbp.infrastructure.modbus.frame.pdu.WriteSingleRequestPdu;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

@Slf4j
public class ModbusTcpSimulator {
    private final ServerSocket serverSocket;
    private final int[] registers;

    @Getter
    private volatile boolean running;

    private ModbusTcpSimulator(int port, int registerCount) {
        try {
            serverSocket = new ServerSocket(port);
            registers = new int[registerCount];
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ModbusTcpSimulator create(int port, int registerCount) {
        return new ModbusTcpSimulator(port, registerCount);
    }

    public void start() {
        running = true;

        Thread.ofVirtual().start(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Socket client = serverSocket.accept();

                    handleClient(client);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void stop() {
        try {
            serverSocket.close();

            running = false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int getRegister(int address) {
        return registers[address];
    }

    private void handleClient(Socket client) {
        Thread.ofVirtual().start(() -> {
                try (DataInputStream in = new DataInputStream(new BufferedInputStream(client.getInputStream()));
                     DataOutputStream out = new DataOutputStream(new BufferedOutputStream(client.getOutputStream()))) {
                    while (!Thread.currentThread().isInterrupted()) {

                        MBAPHeader requestHeader = MBAPHeader.read(in);

                        int functionCode = in.readUnsignedByte();

                        ModbusPdu modbusPdu = ModbusPdu.readRequest(in, functionCode);

                        switch (modbusPdu) {
                            case ReadHoldingRequestPdu requestPdu -> handleReadHoldingRequest(out, requestHeader, requestPdu);
                            case WriteSingleRequestPdu requestPdu -> handleWriteSingleRequest(out, requestHeader, requestPdu);
                            default -> handleErrorRequest(out);
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
        });
    }

    private void handleReadHoldingRequest(DataOutputStream out, MBAPHeader requestHeader, ReadHoldingRequestPdu requestPdu) throws IOException {
        int quantity = requestPdu.quantity();
        int startAddress = requestPdu.startAddress();

        int byteCount = quantity * 2;

        int responsePduLength = 2 + byteCount;

        ByteBuffer buffer = ByteBuffer.allocate(quantity * 2);

        for (int i = startAddress; i < startAddress + quantity; i++) {
            buffer.putShort((short) registers[i]);
        }

        MBAPHeader responseHeader = MBAPHeader.createResponse(requestHeader, responsePduLength);
        ReadHoldingResponsePdu responsePdu = new ReadHoldingResponsePdu(byteCount, buffer.array());

        out.write(responseHeader.encode());
        out.write(responsePdu.encode());
        out.flush();
    }

    private void handleWriteSingleRequest(DataOutputStream out, MBAPHeader requestHeader, WriteSingleRequestPdu requestPdu) throws IOException {
        int address = requestPdu.address();
        int value = requestPdu.value();

        registers[address] = value;

        out.write(requestHeader.encode());
        out.write(requestPdu.encode());
        out.flush();
    }

    private void handleErrorRequest(DataOutputStream out) {

    }
}
