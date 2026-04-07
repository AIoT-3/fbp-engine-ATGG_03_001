package com.nhnacademy.fbp.core.node.impl;

import ch.qos.logback.core.util.ExecutorServiceUtil;
import com.nhnacademy.fbp.core.messsage.Message;
import com.nhnacademy.fbp.core.node.AbstractNode;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimerNode extends AbstractNode {
    private final long intervalMs;
    private int tickCount;
    private ScheduledExecutorService scheduler;

    private TimerNode(String id, long intervalMs) {
        super(id);
        this.intervalMs = intervalMs;
        tickCount = 0;

        addOutputPort("out");
    }

    public static TimerNode create(String id, long intervalMs) {
        return new TimerNode(id, intervalMs);
    }

    @Override
    public void initialize() {
        scheduler = ExecutorServiceUtil.newScheduledExecutorService();

        scheduler.scheduleAtFixedRate(() ->
                send("out", Message.create()
                        .withEntry("tick", ++tickCount)
                        .withEntry("timestamp", System.currentTimeMillis())),
                0, intervalMs, TimeUnit.MILLISECONDS
        );
    }

    @Override
    protected void onProcess(Message message) {
        // TimerNode는 외부에서 메시지를 받지 않으므로 사용하지 않음.
    }

    @Override
    public void shutdown() {
        if (scheduler != null) {
            scheduler.shutdown();
        }
    }
}
