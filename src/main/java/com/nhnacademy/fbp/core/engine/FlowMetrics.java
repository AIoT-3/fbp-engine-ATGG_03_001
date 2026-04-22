package com.nhnacademy.fbp.core.engine;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class FlowMetrics implements MetricCollector {
    private final String flowId;
    private final NodeMetrics summary = NodeMetrics.create();
    private final Map<String, NodeMetrics> registry = new ConcurrentHashMap<>();

    private FlowMetrics(String flowId) {
        this.flowId = flowId;
    }

    public static FlowMetrics create(String flowId) {
        return new FlowMetrics(flowId);
    }

    @Override
    public void record(String nodeId, long duration) {
        registry.computeIfAbsent(nodeId, k -> NodeMetrics.create()).record(duration);
        summary.record(duration);
    }

    @Override
    public void recordError(String nodeId) {
        registry.computeIfAbsent(nodeId, k -> NodeMetrics.create()).recordError();
        summary.recordError();
    }

    public Optional<NodeMetrics> getNodeMetric(String nodeId) {
        return Optional.ofNullable(registry.get(nodeId));
    }

    public NodeMetrics getFlowSummary() {
        return summary;
    }
}
