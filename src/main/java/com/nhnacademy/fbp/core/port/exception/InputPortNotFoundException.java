package com.nhnacademy.fbp.core.port.exception;

import com.nhnacademy.fbp.common.exception.BusinessException;
import com.nhnacademy.fbp.common.exception.ErrorCode;

public class InputPortNotFoundException extends BusinessException {
    public InputPortNotFoundException() {
        super(ErrorCode.INPUT_PORT_NOT_FOUND);
    }
}
