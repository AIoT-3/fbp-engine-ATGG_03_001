package com.nhnacademy.fbp.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    NODE_NOT_FOUND("존재하지 않는 노드입니다."),

    INPUT_PORT_NOT_FOUND("존재하지 않는 입력 포트입니다."),
    OUTPUT_PORT_NOT_FOUND("존재하지 않는 출력 포트입니다."),

    FLOW_NOT_FOUND("존재하지 않는 플로우입니다."),
    FLOW_NOT_VALIDATION("플로우 검증에 실패하였습니다."),

    CIRCULAR_CONNECTION("순환 연결이 감지되었습니다.");

    private final String message;
}
