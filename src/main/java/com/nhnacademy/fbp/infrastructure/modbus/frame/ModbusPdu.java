package com.nhnacademy.fbp.infrastructure.modbus.frame;

import com.nhnacademy.fbp.infrastructure.modbus.exception.ModbusException;
import com.nhnacademy.fbp.infrastructure.modbus.frame.pdu.ReadHoldingRequestPdu;
import com.nhnacademy.fbp.infrastructure.modbus.frame.pdu.ReadHoldingResponsePdu;
import com.nhnacademy.fbp.infrastructure.modbus.frame.pdu.WriteSingleRequestPdu;
import com.nhnacademy.fbp.infrastructure.modbus.frame.pdu.WriteSingleResponsePdu;

import java.io.DataInputStream;
import java.io.IOException;

public interface ModbusPdu {
    int getFunctionCode();
    byte[] encode();
    int getLength();

    static ModbusPdu readRequest(DataInputStream in) throws IOException {
        int functionCode = readFunctionCode(in);

        return switch (functionCode) {
            case 0x03 -> ReadHoldingRequestPdu.read(in);
            case 0x06 -> WriteSingleRequestPdu.read(in);
            default -> throw new ModbusException(functionCode, 0x01);
        };
    }

    static ModbusPdu readResponse(DataInputStream in, MBAPHeader responseHeader) throws IOException {
        int functionCode = readFunctionCode(in);

        return switch (functionCode) {
            case 0x03 -> ReadHoldingResponsePdu.read(in, responseHeader.length() - 2); // unitId, FC 이미 읽음
            case 0x06 -> WriteSingleResponsePdu.read(in);
            default -> throw new ModbusException(functionCode, 0x01);
        };
    }

    private static int readFunctionCode(DataInputStream in) throws IOException {
        int functionCode = in.readUnsignedByte();

        if (functionCode >= 0x80) {
            int exceptionCode = in.readUnsignedByte();

            throw new ModbusException(functionCode, exceptionCode);
        }

        return functionCode;
    }
}
