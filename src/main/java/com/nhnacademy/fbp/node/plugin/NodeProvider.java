package com.nhnacademy.fbp.node.plugin;

import com.nhnacademy.fbp.core.node.AbstractNode;

import java.util.Map;

public interface NodeProvider {
    String getNodeType();
    AbstractNode create(String id, Map<String, Object> config);
}
