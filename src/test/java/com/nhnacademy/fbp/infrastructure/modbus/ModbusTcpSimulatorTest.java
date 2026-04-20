package com.nhnacademy.fbp.infrastructure.modbus;

import com.nhnacademy.fbp.infrastructure.modbus.exception.ModbusException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("integration")
class ModbusTcpSimulatorTest {
    @Test
    @DisplayName("레지스터 범위를 벗어나는 값을 읽으려고 하면 ModbusException이 발생한다.")
    void getRegisters_WhenOutOfBounds_ThrowsException() {
        // given
        ModbusTcpSimulator simulator = ModbusTcpSimulator.create(8080, 1);

        // when & then
        assertThatThrownBy(() -> simulator.getRegisters(0, 5))
                .isInstanceOf(ModbusException.class)
                .hasFieldOrPropertyWithValue("functionCode", 0x83)
                .hasFieldOrPropertyWithValue("exceptionCode", 0x02);
    }

    @Test
    @DisplayName("레지스터 범위를 벗어나는 값을 쓰려고 하면 ModbusException이 발생한다.")
    void setRegister_WhenOutOfBounds_ThrowsException() {
        // given
        ModbusTcpSimulator simulator = ModbusTcpSimulator.create(8081, 1);

        // when & then
        assertThatThrownBy(() -> simulator.setRegister(5, 500))
                .isInstanceOf(ModbusException.class)
                .hasFieldOrPropertyWithValue("functionCode", 0x86)
                .hasFieldOrPropertyWithValue("exceptionCode", 0x02);
    }
}