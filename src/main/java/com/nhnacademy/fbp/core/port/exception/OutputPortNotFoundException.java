package com.nhnacademy.fbp.core.port.exception;

import com.nhnacademy.fbp.common.exception.BusinessException;
import com.nhnacademy.fbp.common.exception.ErrorCode;

public class OutputPortNotFoundException extends BusinessException {
    public OutputPortNotFoundException() {
        super(ErrorCode.OUTPUT_PORT_NOT_FOUND);
    }
}
