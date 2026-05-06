package com.nhnacademy.fbp.node.standard;

import com.nhnacademy.fbp.core.message.Message;
import com.nhnacademy.fbp.core.node.AbstractNode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ErrorHandlerNode extends AbstractNode {
    private final int maxRetries;

    private ErrorHandlerNode(String id, int maxRetries) {
        super(id);
        this.maxRetries = maxRetries;
        
        addInputPort("in");
        addOutputPort("retry");
        addOutputPort("fail");
    }

    public static ErrorHandlerNode create(String id, int maxRetries) {
        return new ErrorHandlerNode(id, maxRetries);
    }

    public static ErrorHandlerNode create(String id) {
        return create(id, 3);
    }

    @Override
    protected void onProcess(Message message) {
        log.error("에러 핸들러 수신: {}", message.getPayload().get("message"));
        
        int retryCount = (int) message.getPayload().getOrDefault("retryCount", 0);
        
        if (retryCount < maxRetries) {
            log.info("재시도 시도 중... ({} / {})", retryCount + 1, maxRetries);
            message.getPayload().put("retryCount", retryCount + 1);
            send("retry", message);
        } else {
            log.warn("최대 재시도 횟수 초과. 실패 포트로 전송.");
            send("fail", message);
        }
    }
}
