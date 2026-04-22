package com.nhnacademy.fbp.core.engine;

public interface MetricCollector {
    void record(String nodeId, long duration);
    void recordError(String nodeId);
}
