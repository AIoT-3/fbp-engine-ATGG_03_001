package com.nhnacademy.fbp.node.standard;

import com.nhnacademy.fbp.core.connection.Connection;
import com.nhnacademy.fbp.core.message.Message;
import com.nhnacademy.fbp.core.port.InputPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DynamicRouterNodeTest {
    private DynamicRouterNode routerNode;
    private TestInputPort matchPort;
    private TestInputPort defaultPort;

    @BeforeEach
    void setUp() {
        List<RoutingRule> rules = List.of(
                new RoutingRule("type", "==", "alert", "alertPort"),
                new RoutingRule("value", ">", 100, "criticalPort")
        );
        routerNode = DynamicRouterNode.create("router", rules);

        matchPort = new TestInputPort("match");
        defaultPort = new TestInputPort("default");

        // 연결 설정
        routerNode.getOutputPort("alertPort").connect(Connection.create("conn1"));
        routerNode.getOutputPort("alertPort").getConnections().get(0).setTarget(matchPort);

        routerNode.getOutputPort("default").connect(Connection.create("conn2"));
        routerNode.getOutputPort("default").getConnections().get(0).setTarget(defaultPort);
    }

    @Test
    @DisplayName("규칙에 매칭되면 해당 포트로 메시지를 전달한다.")
    void onProcess_WhenMatched_SendsToTargetPort() {
        Message msg = Message.create().withEntry("type", "alert");
        routerNode.process(msg);

        assertThat(matchPort.receivedMessage).isNotNull();
        assertThat(matchPort.receivedMessage.getPayload().get("type")).isEqualTo("alert");
    }

    @Test
    @DisplayName("매칭되는 규칙이 없으면 default 포트로 전달한다.")
    void onProcess_WhenNotMatched_SendsToDefaultPort() {
        Message msg = Message.create().withEntry("type", "normal");
        routerNode.process(msg);

        assertThat(defaultPort.receivedMessage).isNotNull();
    }

    private static class TestInputPort implements InputPort {
        private final String name;
        Message receivedMessage;

        TestInputPort(String name) { this.name = name; }
        @Override public void receive(Message message) { this.receivedMessage = message; }
        @Override public String getName() { return name; }
        @Override public com.nhnacademy.fbp.core.node.AbstractNode getOwner() { return null; }
        @Override public Message take() { return null; }
        @Override public int getBufferSize() { return 0; }
    }
}
