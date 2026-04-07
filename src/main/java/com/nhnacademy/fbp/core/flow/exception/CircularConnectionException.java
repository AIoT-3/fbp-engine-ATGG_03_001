package com.nhnacademy.fbp.core.flow.exception;

import com.nhnacademy.fbp.common.exception.BusinessException;
import com.nhnacademy.fbp.common.exception.ErrorCode;

public class CircularConnectionException extends BusinessException {
    public CircularConnectionException() {
        super(ErrorCode.CIRCULAR_CONNECTION);
    }
}
