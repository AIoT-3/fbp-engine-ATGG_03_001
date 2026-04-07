package com.nhnacademy.fbp.core.flow.exception;

import com.nhnacademy.fbp.common.exception.BusinessException;
import com.nhnacademy.fbp.common.exception.ErrorCode;

public class FlowNotFoundException extends BusinessException {
    public FlowNotFoundException() {
        super(ErrorCode.FLOW_NOT_FOUND);
    }
}
