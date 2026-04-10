package com.nhnacademy.fbp.infrastructure.modbus.frame.pdu;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class ReadHoldingResponsePduTest {

    @Test
    @DisplayName("생성된 PDU를 인코딩하면, Modbus PDU 스펙와 일치한다.")
    void encode_WhenCreateAndEncode_CorrectsModbusPdu() {
        // given
        byte[] data = { 0x00, 0x01, 0x00, 0x02, 0x00, 0x03 };

        ReadHoldingResponsePdu pdu = new ReadHoldingResponsePdu(data.length, data);

        byte[] fcAndByteCount = { 0x03, 0x06 };

        byte[] expected = new byte[fcAndByteCount.length + data.length];

        System.arraycopy(fcAndByteCount, 0, expected, 0, fcAndByteCount.length);
        System.arraycopy(data, 0, expected, fcAndByteCount.length, data.length);

        // when
        byte[] actual = pdu.encode();

        // then
        assertThat(actual)
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("Modbus PDU 바이트 배열을 읽으면, ReadHoldingResonsePdu 객체로 매핑할 수 있다.")
    void read_WhenPduByteArray_ReturnPduEntity() throws IOException {
        // given
        byte[] given = { 0x03, 0x06, 0x00, 0x01, 0x00, 0x02, 0x00, 0x03 };

        DataInputStream in = new DataInputStream(new ByteArrayInputStream(given));

        int functionCode = in.readUnsignedByte();

        // when
        ReadHoldingResponsePdu pdu = ReadHoldingResponsePdu.read(in, given.length - 1);

        // then
        ByteBuffer buffer = ByteBuffer.wrap(given);

        // FC는 먼저 읽혀서 분기 처리에 사용됨
        buffer.get();

        assertSoftly(softly -> {
            softly.assertThat(pdu.getFunctionCode())
                    .isEqualTo(functionCode);
            softly.assertThat(pdu.byteCount())
                    .isEqualTo(buffer.get());
        });

        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);

        assertThat(pdu.data())
                .isEqualTo(data);
    }

    @Test
    @DisplayName("바이트 배열의 남은 길이가 Byte Count의 바이트 길이와 데이터 배열 길이의 합이 아니라면 IOException이 발생한다.")
    void read_WhenInvalidRemainingLength_ThrowsException() throws IOException {
        // given
        byte[] given = { 0x03, 0x06, 0x00, 0x01, 0x00, 0x02, 0x00, 0x03 };

        DataInputStream in = new DataInputStream(new ByteArrayInputStream(given));

        in.readUnsignedByte(); // Function Count

        // when & then
        assertThatThrownBy(() -> ReadHoldingResponsePdu.read(in, 999))
                .isInstanceOf(IOException.class);
    }
}