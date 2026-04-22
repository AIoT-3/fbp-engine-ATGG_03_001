package com.nhnacademy.fbp.core.flow;

import com.nhnacademy.fbp.core.connection.Connection;
import com.nhnacademy.fbp.core.flow.exception.FlowValidationException;
import com.nhnacademy.fbp.core.node.AbstractNode;
import com.nhnacademy.fbp.core.node.exception.NodeNotFoundException;
import com.nhnacademy.fbp.node.standard.LogNode;
import com.nhnacademy.fbp.core.port.exception.InputPortNotFoundException;
import com.nhnacademy.fbp.core.port.exception.OutputPortNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

class FlowTest {

    static class FlowFixture {
        static final String SOURCE_ID = "source";
        static final String TARGET_ID = "target";
        static final String SOURCE_OUTPUT_ID = "out";
        static final String TARGET_INPUT_ID = "in";

        static AbstractNode defaultSource() {
            return LogNode.create(SOURCE_ID);
        }

        static AbstractNode defaultTarget() {
            return LogNode.create(TARGET_ID);
        }

        static Flow defaultFlow() {
            return Flow.create("test", "test");
        }
    }

    @Test
    @DisplayName("노드가 등록되면, getNodes()로 해당 노드를 조회할 수 있다.")
    void addNode_WhenAddNodeAndGetNodes_ReturnsNodes() {
        // given
        Flow flow = FlowFixture.defaultFlow();

        AbstractNode node = mock(AbstractNode.class);

        // when
        flow.addNode(node);

        Map<String, AbstractNode> nodes = flow.getNodes();

        // then
        assertThat(nodes.values())
                .isNotNull()
                .hasSize(1)
                .contains(node);
    }

    @Test
    @DisplayName("연결이 등록되면, getConnections()로 해당 연결을 조회할 수 있다.")
    void connect_WhenAddConnectionAndGetConnections_ReturnsConnections() {
        // given
        AbstractNode source = FlowFixture.defaultSource();
        AbstractNode target = FlowFixture.defaultTarget();

        Flow flow = FlowFixture.defaultFlow()
                .addNode(source)
                .addNode(target);

        // when
        flow.connect(FlowFixture.SOURCE_ID, FlowFixture.TARGET_ID);

        List<Connection> connections = flow.getConnections();

        // then
        assertThat(connections)
                .isNotNull()
                .hasSize(1);
    }

    @Test
    @DisplayName("존재하지 않는 소스 노드로 연결을 시도하면, NodeNotFoundException이 발생한다.")
    void connect_WhenSourceNotExists_ThrowsException() {
        // given
        String sourceId = "non-existent-node";
        AbstractNode target = FlowFixture.defaultTarget();

        Flow flow = FlowFixture.defaultFlow()
                .addNode(target);

        // when & then
        assertThatThrownBy(() -> flow.connect(sourceId, FlowFixture.TARGET_ID))
                .isInstanceOf(NodeNotFoundException.class);
    }

    @Test
    @DisplayName("존재하지 않는 타겟 노드로 연결을 시도하면, NodeNotFoundException이 발생한다.")
    void connect_WhenTargetNotExists_ThrowsException() {
        // given
        String targetId = "non-existent-node";
        AbstractNode source = FlowFixture.defaultSource();

        Flow flow = FlowFixture.defaultFlow()
                .addNode(source);

        // when & then
        assertThatThrownBy(() -> flow.connect(FlowFixture.SOURCE_ID, targetId))
                .isInstanceOf(NodeNotFoundException.class);
    }

    @Test
    @DisplayName("존재하지 않는 소스 포트로 연결을 시도하면, OutputPortNotFoundException이 발생한다.")
    void connect_WhenSourcePortNotExists_ThrowsException() {
        // given
        String sourceOutputId = "non-existent-port";

        AbstractNode source = FlowFixture.defaultSource();
        AbstractNode target = FlowFixture.defaultTarget();

        Flow flow = FlowFixture.defaultFlow()
                .addNode(source)
                .addNode(target);

        // when & then
        assertThatThrownBy(() -> flow.connect(FlowFixture.SOURCE_ID, sourceOutputId, FlowFixture.TARGET_ID, FlowFixture.TARGET_INPUT_ID))
                .isInstanceOf(OutputPortNotFoundException.class);
    }

    @Test
    @DisplayName("존재하지 않는 타겟 포트로 연결을 시도하면, InputPortNotFoundException이 발생한다.")
    void connect_WhenTargetPortNotExists_ThrowsException() {
        // given
        String targetInputId = "non-existent-port";

        AbstractNode source = FlowFixture.defaultSource();
        AbstractNode target = FlowFixture.defaultTarget();

        Flow flow = FlowFixture.defaultFlow()
                .addNode(source)
                .addNode(target);

        // when & then
        assertThatThrownBy(() -> flow.connect(FlowFixture.SOURCE_ID, FlowFixture.SOURCE_OUTPUT_ID, FlowFixture.TARGET_ID, targetInputId))
                .isInstanceOf(InputPortNotFoundException.class);
    }

    @Test
    @DisplayName("노드가 존재하지 않는 경우, validate()를 호출하면 FlowValidationException이 발생한다.")
    void validate_WhenNodeEmpty_ThrowsException() {
        // given
        Flow flow = FlowFixture.defaultFlow();

        // when & then
        assertThatThrownBy(flow::validate)
                .isInstanceOf(FlowValidationException.class);
    }

    @Test
    @DisplayName("유효한 노드와 연결이 존재하는 경우, validate()를 호출하면 아무런 예외가 발생하지 않는다.")
    void validate_WhenValid_ReturnsEmptyList() {
        // given
        AbstractNode node1 = FlowFixture.defaultSource();
        AbstractNode node2 = FlowFixture.defaultTarget();

        Flow flow = FlowFixture.defaultFlow()
                .addNode(node1)
                .addNode(node2)
                .connect(FlowFixture.SOURCE_ID, FlowFixture.TARGET_ID);

        // when & then
        assertDoesNotThrow(flow::validate);
    }

    @Test
    @DisplayName("순환 연결이 존재하는 경우, validate()를 호출하면 FlowValidationException이 발생한다.")
    void validate_WhenCircularConnectionExists_ThrowsException() {
        // given
        AbstractNode node1 = FlowFixture.defaultSource();
        AbstractNode node2 = FlowFixture.defaultTarget();

        Flow flow = FlowFixture.defaultFlow()
                .addNode(node1)
                .addNode(node2)
                .connect(FlowFixture.SOURCE_ID, FlowFixture.TARGET_ID)
                .connect(FlowFixture.TARGET_ID, FlowFixture.SOURCE_ID);

        // when & then
        assertThatThrownBy(flow::validate)
                .isInstanceOf(FlowValidationException.class);
    }

    @Test
    @DisplayName("initialize()를 호출하면, 모든 노드의 initialize()가 호출된다.")
    void initialize_WhenCalled_InitializesAllNodes() {
        // given
        AbstractNode node1 = spy(FlowFixture.defaultSource());
        AbstractNode node2 = spy(FlowFixture.defaultTarget());

        given(node1.getId()).willReturn("1");
        given(node2.getId()).willReturn("2");

        Flow flow = FlowFixture.defaultFlow()
                .addNode(node1)
                .addNode(node2);

        // when
        flow.initialize();

        // then
        then(node1)
                .should()
                .initialize();

        then(node2)
                .should()
                .initialize();
    }

    @Test
    @DisplayName("shutdown()을 호출하면, 모든 노드의 shutdown()이 호출된다.")
    void shutdown_WhenCalled_ShutdownsAllNodes() {
        // given
        AbstractNode node1 = spy(FlowFixture.defaultSource());
        AbstractNode node2 = spy(FlowFixture.defaultTarget());

        given(node1.getId()).willReturn("1");
        given(node2.getId()).willReturn("2");

        Flow flow = FlowFixture.defaultFlow()
                .addNode(node1)
                .addNode(node2);

        // when
        flow.shutdown();

        // then
        then(node1)
                .should()
                .shutdown();

        then(node2)
                .should()
                .shutdown();
    }
}