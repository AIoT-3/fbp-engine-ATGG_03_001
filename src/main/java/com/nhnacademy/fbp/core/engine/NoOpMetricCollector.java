package com.nhnacademy.fbp.core.engine;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NoOpMetricCollector implements MetricCollector {
    private static class Holder {
        private static final MetricCollector INSTANCE = new NoOpMetricCollector();
    }

    public static MetricCollector get() {
        return Holder.INSTANCE;
    }

    @Override
    public void record(String nodeId, long duration) {

    }

    @Override
    public void recordError(String nodeId) {

    }
}
