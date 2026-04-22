package com.nhnacademy.fbp.core.engine.dto;

import com.nhnacademy.fbp.core.engine.NodeMetrics;

public record MetricResponse(
        String id,
        long processed,
        long totalDuration,
        double avgTime,
        long errors
) {
    public static MetricResponse of(String id, NodeMetrics nodeMetrics) {
        long count = nodeMetrics.getCount();
        long totalDuration = nodeMetrics.getTotalDuration();
        double avgTime = (count == 0) ? 0 : (double) totalDuration / count;
        long errorCount = nodeMetrics.getErrorCount();

        return new MetricResponse(id, count, totalDuration, avgTime, errorCount);
    }
}
