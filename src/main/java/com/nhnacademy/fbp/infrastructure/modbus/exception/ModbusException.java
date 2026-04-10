package com.nhnacademy.fbp.infrastructure.modbus.exception;

import lombok.Getter;

@Getter
public class ModbusException extends RuntimeException {
    private final int functionCode;
    private final int exceptionCode;

    public ModbusException(int functionCode, int exceptionCode) {
        super(String.format("MODBUS 에러 - 0x%02X, Exception: 0x%02X", functionCode, exceptionCode));
        this.functionCode = functionCode;
        this.exceptionCode = exceptionCode;
    }
}
