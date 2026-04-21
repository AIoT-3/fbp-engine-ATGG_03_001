package com.nhnacademy.fbp.common.parser;

import com.nhnacademy.fbp.common.parser.exception.NodeParseException;
import com.nhnacademy.fbp.core.node.AbstractNode;
import com.nhnacademy.fbp.node.external.NodeProvider;
import com.nhnacademy.fbp.node.external.PluginLoader;
import com.nhnacademy.fbp.node.standard.TimerNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NodeFactoryTest {

    @Test
    @DisplayName("올바른 NodeConfig으로 실제 노드 객체를 생성할 수 있다.")
    void createNode_WhenValidConfig_ReturnsNode() {
        // given
        NodeFactory nodeFactory = NodeFactory.create(Map.of());
        NodeConfig config = new NodeConfig("timer", "TimerNode", Map.of("intervalMs", 1000));

        // when
        AbstractNode node = config.toNode(nodeFactory);

        // then
        assertThat(node)
                .isNotNull()
                .isInstanceOf(TimerNode.class);
    }

    @Test
    @DisplayName("올바른 NodeConfig으로 플러그인 노드 객체를 생성할 수 있다.")
    void createNode_WhenValidConfig_ReturnsPluginNode() {
        // given
        PluginLoader pluginLoader = PluginLoader.create("src/test/resources/plugin");
        NodeFactory nodeFactory = NodeFactory.create(pluginLoader.getPlugins());
        NodeConfig config = new NodeConfig("hello-world", "HelloWorldNode", Map.of());

        // when
        AbstractNode node = config.toNode(nodeFactory);

        // then
        assertThat(node)
                .isNotNull()
                .isInstanceOf(NodeProvider.class);
    }

    @Test
    @DisplayName("등록되지 않은 노드를 생성하려 하면, NodeParseException이 발생한다.")
    void createNode_WhenNotRegisteredNode_ThrowsException() {
        // given
        NodeFactory nodeFactory = NodeFactory.create(Map.of());
        NodeConfig config = new NodeConfig("not-exists", "Not-Exists", Map.of());

        // when & then
        assertThatThrownBy(() -> config.toNode(nodeFactory))
                .isInstanceOf(NodeParseException.class);
    }

    @Test
    @DisplayName("매개변수가 누락되거나 잘못된 값이 들어가면, 예외가 발생한다.")
    void createNode_WhenInvalidParameter_ThrowsException() {
        // given
        NodeFactory nodeFactory = NodeFactory.create(Map.of());
        NodeConfig config = new NodeConfig("timer", "TimerNode", null);

        // when & then
        assertThatThrownBy(() -> config.toNode(nodeFactory))
                .isInstanceOf(RuntimeException.class);
    }
}