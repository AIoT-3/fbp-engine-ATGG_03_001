package com.nhnacademy.fbp.core.node;

import com.nhnacademy.fbp.core.messsage.Message;
import com.nhnacademy.fbp.core.port.InputPort;
import com.nhnacademy.fbp.core.port.OutputPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.spy;

class AbstractNodeTest {

    static class AbstractNodeFixture {
        static AbstractNode defaultNode() {
            return new AbstractNode("test") {
                @Override
                protected void onProcess(Message message) {
                    // 테스트를 위한 더미 구현체.
                }
            };
        }
    }

    @Test
    @DisplayName("getId()를 호출하면 생성 시 지정한 ID가 반환된다.")
    void getId_WhenGetId_ReturnsId() {
        // given
        String id = "test";
        AbstractNode node = AbstractNodeFixture.defaultNode();

        // when
        String found = node.getId();

        // then
        assertThat(found)
                .isNotNull()
                .isEqualTo(id);
    }

    @Test
    @DisplayName("InputPort를 등록하면 getInputPort()로 꺼낼 수 있다.")
    void addInputPort_WhenAddInputPortAndGetInputPort_ReturnsInputPort() {
        // given
        AbstractNode node = AbstractNodeFixture.defaultNode();

        // when
        String inputPortName = "in";
        node.addInputPort(inputPortName);

        InputPort found = node.getInputPort(inputPortName);

        // then
        assertThat(found)
                .isNotNull();
    }

    @Test
    @DisplayName("OutputPort를 등록하면 getOutputPort()로 꺼낼 수 있다.")
    void addOutputPort_WhenAddOutputPortAndGetOutputPort_ReturnsOutputPort() {
        // given
        AbstractNode node = AbstractNodeFixture.defaultNode();

        // when
        String outputPortName = "out";
        node.addOutputPort(outputPortName);

        OutputPort found = node.getOutputPort(outputPortName);

        // then
        assertThat(found)
                .isNotNull();
    }

    @Test
    @DisplayName("존재하지 않는 포트 이름으로 조회하면 null이 반환된다.")
    void getInputPort_WhenNotExists_ReturnsNull() {
        // given
        AbstractNode node = AbstractNodeFixture.defaultNode();

        // when
        InputPort inputPort = node.getInputPort("non-existent-node");

        // then
        assertThat(inputPort)
                .isNull();
    }

    @Test
    @DisplayName("process()를 호출하면 하위 클래스의 onProcess()가 호출된다.")
    void process_WhenProcess_CallsOnProcess() {
        // given
        AbstractNode node = spy(AbstractNodeFixture.defaultNode());

        Message message = Message.create();

        // when
        node.process(message);

        // then
        then(node)
                .should()
                .onProcess(message);
    }
}