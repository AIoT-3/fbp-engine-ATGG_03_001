package com.nhnacademy.fbp.core.engine.dto;

import com.nhnacademy.fbp.core.flow.Flow;

public record FlowListResponse(
        String id,
        String name,
        String status
) {
    public static FlowListResponse from(Flow flow) {
        return new FlowListResponse(flow.getId(), flow.getName(), flow.getState().name());
    }
}
