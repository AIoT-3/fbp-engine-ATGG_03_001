package com.nhnacademy.fbp.infrastructure.modbus.frame.pdu;

import com.nhnacademy.fbp.infrastructure.modbus.frame.ModbusPdu;

import java.nio.ByteBuffer;

public record ErrorResponsePdu(
        int functionCode,
        int exceptionCode
) implements ModbusPdu {

    @Override
    public int getFunctionCode() {
        return functionCode;
    }

    @Override
    public byte[] encode() {
        return ByteBuffer.allocate(getLength())
                .put((byte) functionCode)
                .put((byte) exceptionCode)
                .array();
    }

    @Override
    public int getLength() {
        return 2;
    }
}
