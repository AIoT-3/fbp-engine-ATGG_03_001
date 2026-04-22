package com.nhnacademy.fbp.core.engine;

import com.nhnacademy.fbp.core.engine.dto.FlowListResponse;
import com.nhnacademy.fbp.core.engine.dto.HealthResponse;
import com.nhnacademy.fbp.core.engine.dto.MetricResponse;
import com.nhnacademy.fbp.core.flow.Flow;
import com.nhnacademy.fbp.core.flow.FlowService;
import com.nhnacademy.fbp.core.flow.exception.FlowNotFoundException;
import com.nhnacademy.fbp.core.node.exception.NodeNotFoundException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Getter
public class FlowEngine {
    private final Map<String, FlowMetrics> registry = new ConcurrentHashMap<>();
    private final FlowService flowService;
    private EngineState state;

    private FlowEngine(FlowService flowService) {
        this.flowService = flowService;
        state = EngineState.INITIALIZED;
    }

    public static FlowEngine create(FlowService flowService) {
        return new FlowEngine(flowService);
    }

    public void register(Flow flow) {
        FlowMetrics metrics = registry.computeIfAbsent(flow.getId(), FlowMetrics::create);
        flow.setupMonitoring(metrics);

        flowService.register(flow);
    }

    public void register(String json) {
        Flow flow = flowService.register(json);

        FlowMetrics metrics = registry.computeIfAbsent(flow.getId(), FlowMetrics::create);
        flow.setupMonitoring(metrics);
    }

    public void remove(String flowId) {
        flowService.stopFlow(flowId);
        flowService.removeFlow(flowId);
        registry.remove(flowId);
    }

    public void startFlow(String flowId) {
        flowService.startFlow(flowId);
    }

    public void stopFlow(String flowId) {
        flowService.stopFlow(flowId);
    }

    public void shutdown() {
        flowService.stopAll();
        state = EngineState.STOPPED;
    }

    public List<String> listFlows() {
        List<Flow> flowList = flowService.getAllFlow();

        return flowList.stream()
                .map(flow -> String.format("[%d] %s %s", flowList.indexOf(flow) + 1, flow.getId(), flow.getState()))
                .toList();
    }

    public MetricResponse getFlowSummary(String flowId) {
        FlowMetrics flowMetrics = getFlowMetric(flowId);

        NodeMetrics summaryMetrics = flowMetrics.getFlowSummary();

        return MetricResponse.of(flowId, summaryMetrics);
    }

    public MetricResponse getNodeMetric(String flowId, String nodeId) {
        FlowMetrics flowMetrics = getFlowMetric(flowId);

        NodeMetrics nodeMetrics = flowMetrics.getNodeMetric(nodeId)
                .orElseThrow(NodeNotFoundException::new);

        return MetricResponse.of(nodeId, nodeMetrics);
    }

    private FlowMetrics getFlowMetric(String flowId) {
        FlowMetrics flowMetrics = registry.get(flowId);

        if (flowMetrics == null) {
            throw new FlowNotFoundException();
        }

        return flowMetrics;
    }

    public HealthResponse getHealth() {
        int flowCount = flowService.getCount();

        return new HealthResponse(state.name(), flowCount);
    }

    public List<FlowListResponse> getAllFlow() {
        return flowService.getAllFlow().stream()
                .map(FlowListResponse::from)
                .toList();
    }
}
