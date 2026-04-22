package com.nhnacademy.fbp.core.engine;

import com.nhnacademy.fbp.core.flow.Flow;
import com.nhnacademy.fbp.core.flow.FlowService;
import com.nhnacademy.fbp.core.flow.exception.FlowNotFoundException;
import com.nhnacademy.fbp.core.node.AbstractNode;
import com.nhnacademy.fbp.core.node.exception.NodeNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class FlowEngineTest {
    @Mock
    private FlowService flowService;

    @InjectMocks
    private FlowEngine flowEngine;

    @Test
    @DisplayName("엔진을 생성하면, 상태가 INITIALIZED로 초기화된다.")
    void created_WhenCalled_InitializesState() {
        // then
        assertThat(flowEngine.getState())
                .isEqualTo(EngineState.INITIALIZED);
    }

    @Test
    @DisplayName("플로우를 등록하면, 플로우의 각 노드에 메트릭 수집 클래스가 등록된다.")
    void register_WhenCalled_RegistersMetrics() {
        // given
        AbstractNode node = mock(AbstractNode.class);
        Flow flow = Flow.create("test", "test").addNode(node);

        // when
        flowEngine.register(flow);

        // then
        then(node)
                .should()
                .setupMonitoring(any(FlowMetrics.class));
    }

    @Test
    @DisplayName("shutdown을 호출하면, 엔진의 상태가 STOPPED로 설정된다.")
    void shutdown_WhenCalled_ChangesState() {
        // when
        flowEngine.shutdown();

        // then
        assertThat(flowEngine.getState())
                .isEqualTo(EngineState.STOPPED);
    }

    @Test
    @DisplayName("존재하지 않는 플로우의 메트릭을 조회하려 하면, FlowNotFoundException이 발생한다.")
    void getFlowSummary_WhenNotExists_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> flowEngine.getFlowSummary("test"))
                .isInstanceOf(FlowNotFoundException.class);
    }

    @Test
    @DisplayName("존재하지 않는 노드의 메트릭을 조회하려 하면, NodeNotFoundException이 발생한다.")
    void getNodeMetric_WhenNotExists_ThrowsException() {
        // given
        Flow flow = Flow.create("test", "test");
        String flowId = flow.getId();
        flowEngine.register(flow);

        // when
        assertThatThrownBy(() -> flowEngine.getNodeMetric(flowId, "test"))
                .isInstanceOf(NodeNotFoundException.class);
    }
}