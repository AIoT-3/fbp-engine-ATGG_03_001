package com.nhnacademy.fbp.infrastructure.modbus.frame.pdu;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class WriteSingleRequestPduTest {

    @Test
    @DisplayName("생성된 PDU를 인코딩하면, Modbus PDU 스펙와 일치한다.")
    void encode_WhenCreateAndEncode_CorrectsModbusPdu() {
        // given
        WriteSingleRequestPdu pdu = new WriteSingleRequestPdu(0, 1);

        // when
        byte[] actual = pdu.encode();

        // then
        // Function Code(1), Register Address(2), Value(2)
        byte[] expected = { 0x06, 0x00, 0x00, 0x00, 0x01 };

        assertThat(actual)
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("Modbus PDU 바이트 배열을 읽으면, WriteSingleRequestPdu 객체로 매핑할 수 있다.")
    void read_WhenPduByteArray_ReturnPduEntity() throws IOException {
        // given
        byte[] given = { 0x06, 0x00, 0x00, 0x00, 0x01 };

        DataInputStream in = new DataInputStream(new ByteArrayInputStream(given));

        int functionCode = in.readUnsignedByte();

        // when
        WriteSingleRequestPdu pdu = WriteSingleRequestPdu.read(in);

        // then
        ByteBuffer buffer = ByteBuffer.wrap(given);

        assertSoftly(softly -> {
            softly.assertThat(pdu.getFunctionCode())
                    .isEqualTo(functionCode);
            softly.assertThat(pdu.address())
                    .isEqualTo(buffer.getShort(1));
            softly.assertThat(pdu.value())
                    .isEqualTo(buffer.getShort(3));
        });
    }
}