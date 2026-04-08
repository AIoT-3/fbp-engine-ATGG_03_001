package com.nhnacademy.fbp.core.engine;

import com.nhnacademy.fbp.core.flow.Flow;
import com.nhnacademy.fbp.core.flow.FlowState;
import com.nhnacademy.fbp.core.flow.exception.FlowNotFoundException;
import com.nhnacademy.fbp.core.flow.exception.FlowValidationException;
import com.nhnacademy.fbp.core.node.AbstractNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;

class FlowEngineTest {

    @Test
    @DisplayName("엔진을 생성하면 초기 상태가 INITIALIZED로 설정된다.")
    void create_WhenCreate_StateIsINITIALIZED() {
        // when
        FlowEngine engine = FlowEngine.create();

        // then
        assertThat(engine.getState())
                .isNotNull()
                .isEqualTo(EngineState.INITIALIZED);
    }

    @Test
    @DisplayName("플로우를 등록하면 getFlows()로 해당 플로우를 조회할 수 있다.")
    void register_WhenRegisterFlowAndGetFlows_ReturnsFlows() {
        // given
        String flowId = "test";

        FlowEngine engine = FlowEngine.create();
        Flow flow = Flow.create(flowId);

        // when
        engine.register(flow);

        // then
        assertThat(engine.getFlows())
                .isNotNull()
                .isNotEmpty()
                .extracting(map -> map.get(flowId))
                .isEqualTo(flow);
    }

    @Test
    @DisplayName("플로우를 시작하면 엔진의 상태가 RUNNING으로 변경된다.")
    void startFlow_WhenStartFlow_StateIsRUNNING() {
        // given
        String flowId = "test";

        FlowEngine engine = FlowEngine.create();
        Flow flow = Flow.create(flowId)
                        .addNode(mock(AbstractNode.class));

        engine.register(flow);

        EngineState initialState = engine.getState();

        // when
        engine.startFlow(flowId);

        EngineState afterState = engine.getState();

        // then
        assertSoftly(softly -> {
            softly.assertThat(initialState)
                    .isEqualTo(EngineState.INITIALIZED);
            softly.assertThat(afterState)
                    .isEqualTo(EngineState.RUNNING);
        });
    }

    @Test
    @DisplayName("존재하지 않는 플로우를 시작하려 하면 FlowNotFoundException이 발생한다.")
    void startFlow_WhenNotExists_ThrowsException() {
        // given
        String flowId = "non-existent-flow";

        FlowEngine engine = FlowEngine.create();

        // when & then
        assertThatThrownBy(() -> engine.startFlow(flowId))
                .isInstanceOf(FlowNotFoundException.class);
    }

    @Test
    @DisplayName("유효하지 않은 플로우를 시작하려 하면 FlowValidationException이 발생한다.")
    void startFlow_WhenNotValid_ThrowsException() {
        // given
        String flowId = "test";
        Flow flow = Flow.create(flowId);

        FlowEngine engine = FlowEngine.create();

        engine.register(flow);

        // when & then
        assertThatThrownBy(() -> engine.startFlow(flowId))
                .isInstanceOf(FlowValidationException.class);
    }

    @Test
    @DisplayName("엔진을 정지하면 엔진의 상태가 STOPPED으로 변경된다.")
    void shutdown_WhenCalled_StateIsSTOPPED() {
        // given
        FlowEngine engine = FlowEngine.create();

        // when
        engine.shutdown();

        // then
        assertThat(engine.getState())
                .isEqualTo(EngineState.STOPPED);
    }

    @Test
    @DisplayName("등록된 플로우 중 하나를 정지하면 해당 플로우가 정지되고, 다른 플로우는 영향을 받지 않는다.")
    void stopFlow_WhenStopOne_AnotherUnaffected() {
        // given
        String flowId1 = "test1";
        String flowId2 = "test2";

        Flow flow1 = Flow.create(flowId1)
                .addNode(mock(AbstractNode.class));
        Flow flow2 = Flow.create(flowId2)
                .addNode(mock(AbstractNode.class));

        FlowEngine engine = FlowEngine.create();

        engine.register(flow1);
        engine.register(flow2);

        engine.startFlow(flowId1);
        engine.startFlow(flowId2);

        // when
        engine.stopFlow(flowId1);

        // then
        assertSoftly(softly -> {
            softly.assertThat(flow1.getState())
                    .isEqualTo(FlowState.STOPPED);
            softly.assertThat(flow2.getState())
                    .isEqualTo(FlowState.RUNNING);
        });
    }

    @Test
    @DisplayName("listFlows()를 호출하면 등록된 모든 플로우의 ID와 상태가 조회된다.")
    void listFlow_WhenCalled_ReturnsFlowStatusList() {
        // given
        String flowId1 = "test1";
        String flowId2 = "test2";

        Flow flow1 = Flow.create(flowId1)
                .addNode(mock(AbstractNode.class));
        Flow flow2 = Flow.create(flowId2)
                .addNode(mock(AbstractNode.class));

        FlowEngine engine = FlowEngine.create();

        engine.register(flow1);
        engine.register(flow2);

        // when & then
        assertThat(engine.listFlows())
                .isNotNull()
                .isNotEmpty();
    }
}