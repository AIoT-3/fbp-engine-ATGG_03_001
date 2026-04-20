package com.nhnacademy.fbp.infrastructure.modbus;

import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Tag("integration")
class ModbusTcpClientIT {
    private ModbusTcpSimulator simulator;
    private ModbusTcpClient client;

    private static final int TEST_PORT = 15020;
    private static final int REGISTER_COUNT = 100;

    @BeforeEach
    void setUp() {
        simulator = ModbusTcpSimulator.create(TEST_PORT, REGISTER_COUNT);
        simulator.start();

        client = ModbusTcpClient.create("localhost", TEST_PORT);
        client.connect();
    }

    @AfterEach
    void tearDown() {
        if (client != null) {
            client.disconnect();
        }

        if (simulator != null) {
            simulator.stop();
        }
    }

    @Test
    @DisplayName("클라이언트가 값을 쓰면, 서버의 레지스터 값이 변경되어야 한다.")
    void writeSingleRegister_WhenCalled_ChangesRegisterValue() {
        // given
        int address = 10;
        int expectedValue = 12345;

        // when
        assertDoesNotThrow(() -> client.writeSingleRegister(1, address, expectedValue));

        // then
        assertThat(simulator.getRegister(address))
                .isEqualTo(expectedValue);
    }

    @Test
    @DisplayName("클라이언트가 값을 읽으면, 성공적으로 서버의 레지스터 값들을 읽어와야 한다.")
    void readHoldingRegisters_WhenCalled_ReadsRegisterValues() {
        // given
        simulator.setRegister(0, 100);
        simulator.setRegister(1, 200);
        simulator.setRegister(2, 300);

        // when
        int[] result = client.readHoldingRegisters(1, 0, 3);

        // then
        assertThat(result)
                .hasSize(3)
                .containsExactly(100, 200, 300);
    }
}