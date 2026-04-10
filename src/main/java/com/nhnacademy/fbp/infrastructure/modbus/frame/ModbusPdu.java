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

    static ModbusPdu readRequest(DataInputStream in, int functionCode) throws IOException {
        return switch (functionCode) {
            case 0x03 -> ReadHoldingRequestPdu.read(in);
            case 0x06 -> WriteSingleRequestPdu.read(in);
            default -> throw new ModbusException(functionCode, 0x01);
        };
    }

    static ModbusPdu readResponse(DataInputStream in, int functionCode, int remainingLength) throws IOException {
        return switch (functionCode) {
            case 0x03 -> ReadHoldingResponsePdu.read(in, remainingLength);
            case 0x06 -> WriteSingleResponsePdu.read(in);
            default -> throw new ModbusException(functionCode, 0x01);
        };
    }
}
