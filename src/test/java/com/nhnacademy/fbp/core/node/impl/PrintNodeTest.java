package com.nhnacademy.fbp.core.node.impl;

import com.nhnacademy.fbp.core.messsage.Message;
import com.nhnacademy.fbp.core.node.AbstractNode;
import com.nhnacademy.fbp.core.port.InputPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class PrintNodeTest {

    @Test
    @DisplayName("PrintNode를 생성하면 'in' 이름을 가진 InputPort가 같이 생성된다.")
    void create_WhenCreate_CreatesWithInputPort() {
        // given & when
        PrintNode printNode = PrintNode.create("test");
        InputPort inputPort = printNode.getInputPort("in");

        // then
        assertThat(inputPort)
                .isNotNull()
                .extracting(InputPort::getName)
                .isEqualTo("in");
    }

    @Test
    @DisplayName("process()를 호출하면 예외 없이 메시지가 처리된다.")
    void process_WhenCalled_DoesNotThrow() {
        // given
        PrintNode printNode = PrintNode.create("test");

        // when & then
        assertDoesNotThrow(() -> printNode.process(Message.create()));
    }

    @Test
    @DisplayName("instanceof AbstractNode를 만족한다.")
    void create_WhenCreate_InstanceOfAbstractNode() {
        // given & when
        PrintNode printNode = PrintNode.create("test");

        // then
        assertThat(printNode)
                .isNotNull()
                .isInstanceOf(AbstractNode.class);
    }
}