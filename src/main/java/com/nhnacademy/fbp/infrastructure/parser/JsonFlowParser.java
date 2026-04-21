package com.nhnacademy.fbp.infrastructure.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.fbp.common.util.JsonUtils;
import com.nhnacademy.fbp.core.flow.Flow;
import com.nhnacademy.fbp.core.parser.FlowConfig;
import com.nhnacademy.fbp.core.parser.FlowParser;
import com.nhnacademy.fbp.core.parser.NodeFactory;
import com.nhnacademy.fbp.core.parser.exception.FlowParseException;

import java.io.IOException;
import java.io.InputStream;

public final class JsonFlowParser implements FlowParser {
    private final NodeFactory nodeFactory;
    private final ObjectMapper objectMapper;

    private JsonFlowParser(NodeFactory nodeFactory) {
        this.nodeFactory = nodeFactory;
        this.objectMapper = JsonUtils.get();
    }

    public static JsonFlowParser create(NodeFactory nodeFactory) {
        return new JsonFlowParser(nodeFactory);
    }

    @Override
    public Flow parse(InputStream in) {
        try (in) {
            FlowConfig flowConfig = objectMapper.readValue(in, FlowConfig.class);

            return flowConfig.toFlow(nodeFactory);
        } catch (IOException e) {
            throw new FlowParseException("Flow 파싱 중 오류 발생: " + e.getMessage());
        }
    }

    @Override
    public Flow parse(String fileName) {
        return parse(getInputStream(fileName));
    }

    private InputStream getInputStream(String fileName) {
        return getClass().getClassLoader().getResourceAsStream(fileName);
    }
}
