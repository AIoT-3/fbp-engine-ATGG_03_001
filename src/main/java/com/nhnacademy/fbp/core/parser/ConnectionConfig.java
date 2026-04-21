package com.nhnacademy.fbp.core.parser;

import com.nhnacademy.fbp.core.parser.exception.FlowParseException;
import com.nhnacademy.fbp.core.flow.Flow;

public record ConnectionConfig(
        String from,
        String to
) {
    public void connect(Flow flow) {
        String[] fromParts = from.split(":");
        String[] toParts = to.split(":");

        try {
            flow.connect(fromParts[0], fromParts[1], toParts[0], toParts[1]);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new FlowParseException("연결 형식이 잘못되었습니다.");
        }
    }
}
