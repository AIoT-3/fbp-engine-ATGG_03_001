package com.nhnacademy.fbp.infrastructure.modbus.frame;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public record MBAPHeader(
        int transactionId,
        int protocolId,
        int length,
        int unitId
) {

    public static MBAPHeader createRequest(int transactionId, int length, int unitId) {
        return new MBAPHeader(transactionId, 0, length, unitId);
    }

    public static MBAPHeader createRequest(int transactionId, ModbusPdu pdu, int unitId) {
        return createRequest(transactionId, pdu.getLength() + 1, unitId);
    }

    public static MBAPHeader createResponse(MBAPHeader requestHeader, int length) {
        return new MBAPHeader(requestHeader.transactionId, requestHeader.protocolId, length, requestHeader.unitId);
    }

    public static MBAPHeader createResponse(MBAPHeader requestHeader, ModbusPdu pdu) {
        return createResponse(requestHeader, pdu.getLength() + 1);
    }

    public byte[] encode() {
        return ByteBuffer.allocate(7)
                .putShort((short) transactionId)
                .putShort((short) protocolId)
                .putShort((short) length)
                .put((byte) unitId)
                .array();
    }

    public static MBAPHeader read(DataInputStream in) throws IOException {
        int transactionId = in.readUnsignedShort();
        int protocolId = in.readUnsignedShort();
        int length = in.readUnsignedShort();
        int unitId = in.readUnsignedByte();

        return new MBAPHeader(transactionId, protocolId, length, unitId);
    }
}
