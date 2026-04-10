package com.nhnacademy.fbp.infrastructure.modbus.frame;

import com.nhnacademy.fbp.infrastructure.modbus.frame.pdu.ReadHoldingRequestPdu;
import com.nhnacademy.fbp.infrastructure.modbus.frame.pdu.ReadHoldingResponsePdu;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

class MBAPHeaderTest {

    @Test
    @DisplayName("생성된 MBAP 헤더를 인코딩하면, Modbus 헤더 스펙과 일치한다.")
    void encode_WhenCreateAndEncode_CorrectsModbusHeader() {
        // given
        // PDU = 5바이트  (Function Code(1) + Start Address (2) + Value (2))
        ReadHoldingRequestPdu requestPdu = new ReadHoldingRequestPdu(0, 1);
        MBAPHeader requestHeader = MBAPHeader.createRequest(0, requestPdu, 1);

        // Length = 9바이트 (Unit ID(1) + Function Code(1) + Byte Count(1) + Data(6)
        byte[] data = { 0x00, 0x01, 0x00, 0x02, 0x00, 0x03 };
        ReadHoldingResponsePdu responsePdu = new ReadHoldingResponsePdu(data.length, data);
        MBAPHeader responseHeader = MBAPHeader.createResponse(requestHeader, responsePdu);

        // when
        byte[] encodedRequestHeader = requestHeader.encode();
        byte[] encodedResponseHeader = responseHeader.encode();

        // then
        byte[] expectedRequestHeader = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x06, 0x01 }; // 5 + 1
        byte[] expectedResponseHeader = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x09, 0x01 }; // 8 + 1

        assertSoftly(softly -> {
            softly.assertThat(encodedRequestHeader[5])
                    .isEqualTo((byte) (requestPdu.getLength() + 1));
            softly.assertThat(encodedResponseHeader[5])
                    .isEqualTo((byte) (responsePdu.getLength() + 1));
            softly.assertThat(expectedRequestHeader)
                    .isEqualTo(encodedRequestHeader);
            softly.assertThat(expectedResponseHeader)
                    .isEqualTo(encodedResponseHeader);
        });
    }

    @Test
    @DisplayName("Modbus 헤더 바이트 배열을 읽으면, MBAP 헤더 객체로 매핑할 수 있다.")
    void read_WhenHeaderByteArray_ReturnsMBAPHeader() throws IOException  {
        // given
        byte[] given = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x06, 0x01 };

        DataInputStream in = new DataInputStream(new ByteArrayInputStream(given));

        // when
        MBAPHeader header = MBAPHeader.read(in);

        // then
        ByteBuffer buffer = ByteBuffer.wrap(given);

        assertSoftly(softly -> {
            softly.assertThat(header.transactionId())
                    .isEqualTo(buffer.getShort(0));
            softly.assertThat(header.protocolId())
                    .isEqualTo(buffer.getShort(2));
            softly.assertThat(header.length())
                    .isEqualTo(buffer.getShort(4));
            softly.assertThat(header.unitId())
                    .isEqualTo(buffer.get(6));
        });
    }
}