package com.nhnacademy.fbp.infrastructure.http.controller;

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
    public ResponseEntity<List<String>> getFlowList() {
        List<String> flowList = flowEngine.listFlows();

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
}
