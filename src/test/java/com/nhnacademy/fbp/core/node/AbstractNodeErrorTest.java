package com.nhnacademy.fbp.core.node;

import com.nhnacademy.fbp.core.message.Message;
import com.nhnacademy.fbp.core.port.InputPort;
import com.nhnacademy.fbp.core.connection.Connection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AbstractNodeErrorTest {

    @Test
    @DisplayName("노드 실행 중 예외가 발생하면 error 포트로 에러 메시지를 전송한다.")
    void process_WhenExceptionOccurs_SendsToErrorPort() {
        // given
        AbstractNode failingNode = new AbstractNode("failingNode") {
            @Override
            protected void onProcess(Message message) {
                throw new RuntimeException("Intentional failure");
            }
        };

        TestInputPort errorTarget = new TestInputPort("errorCollector");
        failingNode.getOutputPort("error").connect(Connection.create("errorConn"));
        failingNode.getOutputPort("error").getConnections().get(0).setTarget(errorTarget);

        Message originalMsg = Message.create().withEntry("data", "important");

        // when
        failingNode.process(originalMsg);

        // then
        assertThat(errorTarget.receivedMessage).isNotNull();
        assertThat(errorTarget.receivedMessage.getPayload().get("exception"))
                .isEqualTo("java.lang.RuntimeException");
        assertThat(errorTarget.receivedMessage.getPayload().get("message"))
                .isEqualTo("Intentional failure");
        assertThat(errorTarget.receivedMessage.getPayload().get("nodeId"))
                .isEqualTo("failingNode");
    }

    private static class TestInputPort implements InputPort {
        private final String name;
        Message receivedMessage;
        TestInputPort(String name) { this.name = name; }
        @Override public void receive(Message message) { this.receivedMessage = message; }
        @Override public String getName() { return name; }
        @Override public AbstractNode getOwner() { return null; }
        @Override public Message take() { return null; }
        @Override public int getBufferSize() { return 0; }
    }
}
