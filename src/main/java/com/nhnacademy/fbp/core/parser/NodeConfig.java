package com.nhnacademy.fbp.core.parser;

import com.nhnacademy.fbp.core.parser.exception.FlowParseException;
import com.nhnacademy.fbp.core.node.AbstractNode;

import java.util.Collections;
import java.util.Map;

public record NodeConfig(
        String id,
        String type,
        Map<String, Object> config
) {
    public NodeConfig {
        if (id == null || id.isBlank()) {
            throw new FlowParseException("노드 ID는 필수값입니다.");
        }

        if (type == null || type.isBlank()) {
            throw new FlowParseException("노드 타입은 필수값입니다.");
        }

        if (config == null) {
            config = Collections.emptyMap();
        }
    }

    public AbstractNode toNode(NodeFactory factory) {
        return factory.createNode(this);
    }
}
