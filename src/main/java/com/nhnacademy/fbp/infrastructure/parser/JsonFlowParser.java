package com.nhnacademy.fbp.infrastructure.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.fbp.common.parser.FlowConfig;
import com.nhnacademy.fbp.common.parser.FlowParser;
import com.nhnacademy.fbp.common.parser.NodeFactory;
import com.nhnacademy.fbp.common.parser.exception.FlowParseException;
import com.nhnacademy.fbp.common.util.JsonUtils;
import com.nhnacademy.fbp.core.flow.Flow;

import java.io.IOException;
import java.io.InputStream;

public final class JsonFlowParser implements FlowParser {
    private final NodeFactory nodeFactory;
    private final ObjectMapper objectMapper;

    private JsonFlowParser() {
        this.nodeFactory = NodeFactory.create();
        this.objectMapper = JsonUtils.get();
    }

    public static JsonFlowParser create() {
        return new JsonFlowParser();
    }

    @Override
    public Flow parse(InputStream in) {
        try (in) {
            FlowConfig flowConfig = objectMapper.readValue(in, FlowConfig.class);

            return flowConfig.toFlow(nodeFactory);
        } catch (IOException e) {
            throw new FlowParseException(e);
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
