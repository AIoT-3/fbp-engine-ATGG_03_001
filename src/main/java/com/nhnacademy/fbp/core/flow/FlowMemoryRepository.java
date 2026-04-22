package com.nhnacademy.fbp.core.flow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class FlowMemoryRepository implements FlowRepository {
    private final Map<String, Flow> flows = new ConcurrentHashMap<>();

    @Override
    public List<Flow> findAll() {
        return new ArrayList<>(flows.values());
    }

    @Override
    public Flow save(Flow flow) {
        flows.put(flow.getId(), flow);

        return flow;
    }

    @Override
    public Optional<Flow> findById(String id) {
        return Optional.ofNullable(flows.get(id));
    }

    @Override
    public void deleteById(String id) {
        flows.remove(id);
    }

    @Override
    public boolean existsById(String id) {
        return flows.containsKey(id);
    }

    @Override
    public int count() {
        return flows.size();
    }
}
