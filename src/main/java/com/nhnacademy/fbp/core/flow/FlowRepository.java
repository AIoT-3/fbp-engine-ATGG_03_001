package com.nhnacademy.fbp.core.flow;

import java.util.List;
import java.util.Optional;

public interface FlowRepository {
    List<Flow> findAll();
    Flow save(Flow flow);
    Optional<Flow> findById(String id);
    void deleteById(String id);
    boolean existsById(String id);
    int count();
}
