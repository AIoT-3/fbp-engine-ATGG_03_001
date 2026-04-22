package com.nhnacademy.fbp.core.parser;

import com.nhnacademy.fbp.core.parser.exception.FlowParseException;
import com.nhnacademy.fbp.core.flow.Flow;

import java.util.Collections;
import java.util.List;

public record FlowConfig(
        String id,
        String name,
        String description,
        List<NodeConfig> nodes,
        List<ConnectionConfig> connections
) {
    public FlowConfig {
        if (id == null || id.isBlank()) {
            throw new FlowParseException("노드 ID는 필수값입니다.");
        }

        if (name == null || name.isBlank()) {
            throw new FlowParseException("노드 이름은 필수값입니다.");
        }

        if (nodes == null || nodes.isEmpty()) {
            throw new FlowParseException("노드는 필수입니다.");
        }

        if (connections == null) {
            connections = Collections.emptyList();
        }
    }

    public Flow toFlow(NodeFactory factory) {
        Flow flow = Flow.create(id, name);

        nodes.stream()
                .map(factory::createNode)
                .forEach(flow::addNode);

        connections.forEach(connectionConfig -> connectionConfig.connect(flow));

        return flow;
    }
}
