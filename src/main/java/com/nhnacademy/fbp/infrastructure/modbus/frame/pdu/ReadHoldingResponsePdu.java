package com.nhnacademy.fbp.infrastructure.modbus.frame.pdu;

import com.nhnacademy.fbp.infrastructure.modbus.frame.ModbusPdu;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public record ReadHoldingResponsePdu(
        int byteCount,
        byte[] data
) implements ModbusPdu {

    @Override
    public int getFunctionCode() {
        return 0x03;
    }

    @Override
    public byte[] encode() {
        ByteBuffer buffer = ByteBuffer.allocate(getLength());

        return buffer.put((byte) getFunctionCode())
                .put((byte) byteCount)
                .put(data)
                .array();
    }

    @Override
    public int getLength() {
        return 1 + 1 + data.length;
    }

    public static ReadHoldingResponsePdu read(DataInputStream in, int remainingLength) throws IOException {
        int byteCount = in.readUnsignedByte();

        if (remainingLength != byteCount + 1) {

            throw new IOException("패킷 길이 불일치");
        }

        byte[] data = new byte[byteCount];
        in.readFully(data);

        return new ReadHoldingResponsePdu(byteCount, data);
    }
}
