package com.nhnacademy.fbp.core.port;

import com.nhnacademy.fbp.core.messsage.Message;
import com.nhnacademy.fbp.core.node.AbstractNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

class DefaultInputPortTest {

    @Test
    @DisplayName("receive()를 호출하면 소속된 Node의 receive()가 호출된다.")
    void receive_WhenReceive_CallsNodeReceive() {
        // given
        AbstractNode owner = mock(AbstractNode.class);
        DefaultInputPort inputPort = DefaultInputPort.create("test", owner);

        Message message = Message.create();

        // when
        inputPort.receive(message);

        // then
        then(owner)
                .should()
                .process(message);
    }

    @Test
    @DisplayName("getName()을 호출하면 생성 시 지정한 이름을 반환한다.")
    void getName_WhenGetName_ReturnsName() {
        // given
        AbstractNode owner = mock(AbstractNode.class);
        DefaultInputPort inputPort = DefaultInputPort.create("test", owner);

        // when
        String name = inputPort.getName();

        // then
        assertThat(name)
                .isNotNull()
                .isEqualTo("test");
    }
}