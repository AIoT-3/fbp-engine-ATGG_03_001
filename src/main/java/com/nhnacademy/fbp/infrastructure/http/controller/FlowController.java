package com.nhnacademy.fbp.infrastructure.http.controller;

import com.nhnacademy.fbp.core.engine.dto.FlowListResponse;
import com.nhnacademy.fbp.core.engine.dto.HealthResponse;
import com.nhnacademy.fbp.core.engine.dto.MetricResponse;
import com.nhnacademy.fbp.infrastructure.http.annotation.*;
import com.nhnacademy.fbp.infrastructure.http.dto.ResponseEntity;
import com.nhnacademy.fbp.core.engine.FlowEngine;

import java.util.List;

@RestController
public class FlowController {
    private final FlowEngine flowEngine;

    public FlowController(FlowEngine flowEngine) {
        this.flowEngine = flowEngine;
    }

    @RequestMapping(value = "/flows", method = RequestMethod.GET)
    public ResponseEntity<List<FlowListResponse>> getFlowList() {
        List<FlowListResponse> flowList = flowEngine.getAllFlow();

        return ResponseEntity.of(200, flowList);
    }

    @RequestMapping(value = "/flows", method = RequestMethod.POST)
    public ResponseEntity<Void> createFlow(@RequestBody String json) {
        flowEngine.register(json);

        return ResponseEntity.created();
    }

    @RequestMapping(value = "/flows/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> removeFlow(@PathVariable String id) {
        flowEngine.remove(id);

        return ResponseEntity.noContent();
    }

    @RequestMapping(value = "/flows/{id}/metrics", method = RequestMethod.GET)
    public ResponseEntity<MetricResponse> getFlowSummary(@PathVariable String id) {
        MetricResponse response = flowEngine.getFlowSummary(id);

        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "/flows/{id}/nodes/{nodeId}/metrics", method = RequestMethod.GET)
    public ResponseEntity<MetricResponse> getNodeMetric(@PathVariable String id, @PathVariable String nodeId) {
        MetricResponse response = flowEngine.getNodeMetric(id, nodeId);

        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "/health", method = RequestMethod.GET)
    public ResponseEntity<HealthResponse> getEngineHealth() {
        HealthResponse response = flowEngine.getHealth();

        return ResponseEntity.ok(response);
    }
}
