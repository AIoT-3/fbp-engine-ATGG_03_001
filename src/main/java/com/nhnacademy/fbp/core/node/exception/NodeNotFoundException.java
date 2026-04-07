package com.nhnacademy.fbp.core.node.exception;

import com.nhnacademy.fbp.common.exception.BusinessException;
import com.nhnacademy.fbp.common.exception.ErrorCode;

public class NodeNotFoundException extends BusinessException {
    public NodeNotFoundException() {
        super(ErrorCode.NODE_NOT_FOUND);
    }
}
