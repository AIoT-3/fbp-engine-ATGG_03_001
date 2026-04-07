package com.nhnacademy.fbp.core.flow.exception;

import com.nhnacademy.fbp.common.exception.BusinessException;
import com.nhnacademy.fbp.common.exception.ErrorCode;

public class FlowValidationException extends BusinessException {
    public FlowValidationException() {
        super(ErrorCode.FLOW_NOT_VALIDATION);
    }
}
