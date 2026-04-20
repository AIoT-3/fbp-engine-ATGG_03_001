package com.nhnacademy.fbp.infrastructure.modbus.exception;

public class ModbusConnectException extends RuntimeException {
    public ModbusConnectException(Throwable t) {
        super("ModbusTcpClient 연결 오류: " + t.getMessage());
    }
}
