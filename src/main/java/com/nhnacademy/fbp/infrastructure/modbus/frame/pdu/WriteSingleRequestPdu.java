package com.nhnacademy.fbp.infrastructure.modbus.frame.pdu;

import com.nhnacademy.fbp.infrastructure.modbus.frame.ModbusPdu;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public record WriteSingleRequestPdu(
        int address,
        int value
) implements ModbusPdu {

    @Override
    public int getFunctionCode() {
        return 0x06;
    }

    @Override
    public byte[] encode() {
        return ByteBuffer.allocate(getLength())
                .put((byte) getFunctionCode())
                .putShort((short) address)
                .putShort((short) value)
                .array();
    }

    @Override
    public int getLength() {
        return 5;
    }

    public static WriteSingleRequestPdu read(DataInputStream in) throws IOException {
        int address = in.readUnsignedShort();
        int value = in.readUnsignedShort();

        return new WriteSingleRequestPdu(address, value);
    }
}
