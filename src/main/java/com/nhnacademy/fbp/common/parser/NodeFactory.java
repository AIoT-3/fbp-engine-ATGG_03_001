package com.nhnacademy.fbp.common.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.fbp.common.parser.exception.NodeParseException;
import com.nhnacademy.fbp.common.util.JsonUtils;
import com.nhnacademy.fbp.core.node.AbstractNode;
import com.nhnacademy.fbp.node.external.NodeProvider;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public final class NodeFactory {
    private final Map<String, Class<?>> nodeClasses;
    private final Map<String, NodeProvider> plugins;
    private final ObjectMapper objectMapper;

    private NodeFactory(Map<String, NodeProvider> plugins) {
        objectMapper = JsonUtils.get();
        Reflections reflections = new Reflections("com.nhnacademy.fbp.node");
        nodeClasses = reflections.getSubTypesOf(AbstractNode.class).stream()
                .collect(Collectors.toMap(Class::getSimpleName, clazz -> clazz));

        this.plugins = plugins;
    }

    public static NodeFactory create(Map<String, NodeProvider> plugins) {
        return new NodeFactory(plugins);
    }

    public AbstractNode createNode(NodeConfig nodeConfig) {
        try {
            if (plugins.containsKey(nodeConfig.type())) {
                return plugins.get(nodeConfig.type()).create(nodeConfig.id(), nodeConfig.config());
            }

            Class<?> clazz = nodeClasses.get(nodeConfig.type());

            Method createMethod = Arrays.stream(clazz.getMethods())
                    .filter(m -> "create".equals(m.getName()))
                    .findFirst()
                    .orElseThrow(RuntimeException::new);

            Map<String, Object> config = (nodeConfig.config() == null) ?
                    Map.of() : nodeConfig.config();

            Object[] args = Arrays.stream(createMethod.getParameters())
                    .map(param -> {
                        if (param.getName().equals("id")) return nodeConfig.id();

                        if (!config.containsKey(param.getName())) {
                            throw new NodeParseException("존재하지 않는 설정값입니다: " + param.getName());
                        }

                        Object value = config.get(param.getName());
                        return objectMapper.convertValue(value, param.getType());
                    }).toArray();

            return (AbstractNode) createMethod.invoke(null, args);
        } catch (Exception e) {
            log.error("노드 생성 실패: {}", e.getMessage(), e);
            throw new NodeParseException("노드 생성 실패: " + e.getMessage());
        }
    }
}
