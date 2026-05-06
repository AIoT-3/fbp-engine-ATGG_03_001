package com.nhnacademy.fbp.core.engine;

public interface MetricCollector {
    void recordMessage(String nodeId, long duration);
    void recordError(String nodeId);
}
