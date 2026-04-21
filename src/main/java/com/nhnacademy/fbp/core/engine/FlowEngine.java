package com.nhnacademy.fbp.core.engine;

import com.nhnacademy.fbp.core.flow.Flow;
import com.nhnacademy.fbp.core.flow.exception.FlowNotFoundException;
import com.nhnacademy.fbp.core.flow.exception.FlowValidationException;
import com.nhnacademy.fbp.core.parser.FlowParser;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FlowEngine {
    private final FlowParser flowParser;
    private final Map<String, Flow> flows;
    private EngineState state;

    public static FlowEngine create(FlowParser flowParser) {
        return new FlowEngine(flowParser, new ConcurrentHashMap<>(), EngineState.INITIALIZED);
    }

    public void register(Flow flow) {
        flows.put(flow.getId(), flow);
    }

    public void register(String json) {
        Flow flow = flowParser.parseJson(json);

        register(flow);
    }

    public void remove(String flowId) {
        Flow flow = getFlow(flowId);
        flow.shutdown();
        flows.remove(flowId);
    }

    public void startFlow(String flowId) {
        Flow flow = getFlow(flowId);

        validateFlow(flow);

        flow.initialize();

        state = EngineState.RUNNING;

        log.info("[Engine] 플로우 '{}' 시작됨.", flowId);
    }

    public void stopFlow(String flowId) {
        Flow flow = getFlow(flowId);

        flow.shutdown();

        log.info("[Engine] 플로우 '{}' 정지됨.", flowId);
    }

    public void shutdown() {
        flows.values().forEach(Flow::shutdown);

        state = EngineState.STOPPED;

        log.info("[Engine] 엔진 종료됨.");
    }

    public List<String> listFlows() {
        List<Flow> flowList = new ArrayList<>(flows.values());

        return flowList.stream()
                .map(flow -> String.format("[%d] %s %s", flowList.indexOf(flow) + 1, flow.getId(), flow.getState()))
                .toList();
    }

    private Flow getFlow(String flowId) {
        if (!flows.containsKey(flowId)) {
            throw new FlowNotFoundException();
        }

        return flows.get(flowId);
    }

    private void validateFlow(Flow flow) {
        if (!flow.validate().isEmpty()) {
            throw new FlowValidationException();
        }
    }
}
