package com.nhnacademy.fbp.core.flow;

import com.nhnacademy.fbp.core.flow.exception.FlowAlreadyExistsException;
import com.nhnacademy.fbp.core.flow.exception.FlowNotFoundException;
import com.nhnacademy.fbp.core.parser.FlowParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class FlowService {
    private final FlowRepository flowRepository;
    private final FlowParser flowParser;

    public List<Flow> getAllFlow() {
        return flowRepository.findAll();
    }

    public Flow register(Flow flow) {
        if (flowRepository.existsById(flow.getId())) {
            throw new FlowAlreadyExistsException("이미 존재하는 Flow입니다.");
        }

        return flowRepository.save(flow);
    }

    public Flow register(String json) {
        Flow flow = flowParser.parseJson(json);

        return register(flow);
    }

    public void removeFlow(String id) {
        if (!flowRepository.existsById(id)) {
            throw new FlowNotFoundException();
        }

        flowRepository.deleteById(id);
    }

    public int getCount() {
        return flowRepository.count();
    }

    public void startFlow(String id) {
        Flow flow = getFlow(id);

        flow.validate();

        flow.initialize();

        log.info("[Engine] 플로우 '{}' 시작됨.", id);
    }

    public void stopFlow(String id) {
        Flow flow = getFlow(id);

        flow.shutdown();

        log.info("[Engine] 플로우 '{}' 정지됨.", id);
    }

    public void stopAll() {
        List<Flow> flows = getAllFlow();

        flows.forEach(Flow::shutdown);
    }

    private Flow getFlow(String id) {
        return flowRepository.findById(id)
                .orElseThrow(FlowNotFoundException::new);
    }
}
