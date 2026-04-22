package com.nhnacademy.fbp.core.engine;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.concurrent.atomic.AtomicLong;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NodeMetrics {
    private final AtomicLong count = new AtomicLong(0);
    private final AtomicLong totalDuration = new AtomicLong(0);
    private final AtomicLong errorCount = new AtomicLong(0);

    public static NodeMetrics create() {
        return new NodeMetrics();
    }

    public void record(long duration) {
        count.incrementAndGet();
        totalDuration.addAndGet(duration);
    }

    public void recordError() {
        errorCount.incrementAndGet();
    }

    public long getCount() {
        return count.get();
    }

    public long getTotalDuration() {
        return totalDuration.get();
    }

    public long getErrorCount() {
        return errorCount.get();
    }
}
