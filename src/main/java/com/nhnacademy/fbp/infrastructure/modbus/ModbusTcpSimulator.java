package com.nhnacademy.fbp.infrastructure.modbus;

import com.nhnacademy.fbp.infrastructure.modbus.exception.ModbusException;
import com.nhnacademy.fbp.infrastructure.modbus.frame.*;
import com.nhnacademy.fbp.infrastructure.modbus.frame.pdu.*;
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
            // TODO: 적절한 처리
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
                    // TODO: 적절한 처리
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
            // TODO: 적절한 처리
            throw new RuntimeException(e);
        }
    }

    public int getRegister(int address) {
        return registers[address];
    }

    public byte[] getRegisters(int startAddress, int quantity) {
        if (startAddress < 0 || startAddress + quantity > registers.length - 1) {
            throw new ModbusException(0x03, 0x02);
        }

        ByteBuffer buffer = ByteBuffer.allocate(quantity * 2);

        for (int i = startAddress; i < startAddress + quantity; i++) {
            buffer.putShort((short) registers[i]);
        }

        return buffer.array();
    }

    public void setRegister(int address, int value) {
        if (address > registers.length - 1 || address < 0) {
            throw new ModbusException(0x06, 0x02);
        }

        registers[address] = value;
    }

    private void handleClient(Socket client) {
        Thread.ofVirtual().start(() -> {
                try (DataInputStream in = new DataInputStream(new BufferedInputStream(client.getInputStream()));
                     DataOutputStream out = new DataOutputStream(new BufferedOutputStream(client.getOutputStream()))) {

                    while (!Thread.currentThread().isInterrupted()) {
                        handleRequest(in, out);
                    }
                } catch (IOException e) {
                    // TODO: 적절한 처리
                    throw new RuntimeException(e);
                }
        });
    }

    private void handleRequest(DataInputStream in, DataOutputStream out) throws IOException {
        MBAPHeader requestHeader = MBAPHeader.read(in);

        try {
            ModbusPdu modbusPdu = ModbusPdu.readRequest(in);

            switch (modbusPdu) {
                case ReadHoldingRequestPdu requestPdu ->
                        handleReadHoldingRequest(out, requestHeader, requestPdu);
                case WriteSingleRequestPdu requestPdu ->
                        handleWriteSingleRequest(out, requestHeader, requestPdu);
                default -> throw new ModbusException(modbusPdu.getFunctionCode(), 0x01);
            }
        } catch (ModbusException e) {
            handleErrorRequest(out, requestHeader, e);
        }
    }

    private void handleReadHoldingRequest(DataOutputStream out, MBAPHeader requestHeader, ReadHoldingRequestPdu requestPdu) throws IOException {
        int quantity = requestPdu.quantity();
        int startAddress = requestPdu.startAddress();
        byte[] data = getRegisters(startAddress, quantity);

        ReadHoldingResponsePdu responsePdu = new ReadHoldingResponsePdu(quantity * 2, data);

        sendResponse(out, requestHeader, responsePdu);
    }

    private void handleWriteSingleRequest(DataOutputStream out, MBAPHeader requestHeader, WriteSingleRequestPdu requestPdu) throws IOException {
        int address = requestPdu.address();
        int value = requestPdu.value();

        setRegister(address, value);

        WriteSingleResponsePdu responsePdu = new WriteSingleResponsePdu(address, value);

        sendResponse(out, requestHeader, responsePdu);
    }

    private void handleErrorRequest(DataOutputStream out, MBAPHeader requestHeader, ModbusException e) throws IOException {
        int errorFunctionCode = e.getFunctionCode();
        int exceptionCode = e.getExceptionCode();

        ErrorResponsePdu responsePdu = new ErrorResponsePdu(errorFunctionCode, exceptionCode);

        sendResponse(out, requestHeader, responsePdu);
    }

    private void sendResponse(DataOutputStream out, MBAPHeader requestHeader,  ModbusPdu responsePdu) throws IOException {
        MBAPHeader responseHeader = MBAPHeader.createResponse(requestHeader, responsePdu);

        out.write(responseHeader.encode());
        out.write(responsePdu.encode());
        out.flush();
    }
}
