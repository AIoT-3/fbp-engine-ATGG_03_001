package com.nhnacademy.fbp.infrastructure.modbus.frame.pdu;

import com.nhnacademy.fbp.infrastructure.modbus.frame.ModbusPdu;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public record ReadHoldingRequestPdu(
        int startAddress,
        int quantity
) implements ModbusPdu {

    @Override
    public int getFunctionCode() {
        return 0x03;
    }

    @Override
    public byte[] encode() {
        return ByteBuffer.allocate(getLength())
                .put((byte) getFunctionCode())
                .putShort((short) startAddress)
                .putShort((short) quantity)
                .array();
    }

    @Override
    public int getLength() {
        return 5;
    }

    public static ReadHoldingRequestPdu read(DataInputStream in) throws IOException {
        int startAddress = in.readUnsignedShort();
        int quantity = in.readUnsignedShort();

        return new ReadHoldingRequestPdu(startAddress, quantity);
    }
}
