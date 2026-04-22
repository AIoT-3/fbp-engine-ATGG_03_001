package com.nhnacademy.fbp.node.standard;

import com.nhnacademy.fbp.core.message.Message;
import com.nhnacademy.fbp.core.port.InputPort;
import com.nhnacademy.fbp.core.port.OutputPort;
import com.nhnacademy.fbp.core.utils.FbpTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.function.UnaryOperator;

import static org.assertj.core.api.Assertions.assertThat;

class TransFormNodeTest {
    static class TransformFixture {
        static final UnaryOperator<Message> FUNCTION = message -> {
            double fahrenheit = message.getPayload("temperature");

            double celsius = toCelsius(fahrenheit);

            return message.withEntry("temperature", celsius);
        };
    }

    static double toCelsius(double fahrenheit) {
        return (fahrenheit - 32) * 5.0 / 9.0;
    }

    @Test
    @DisplayName("process()를 호출하면 메시지가 등록된 함수로 변환되어 OutputPort로 전달된다.")
    void process_WhenCalled_TransformsMessageAndSendsToOutputPort() throws InterruptedException{
        // given
        TransformNode transformNode = TransformNode.create("test", TransformFixture.FUNCTION);
        OutputPort outputPort = transformNode.getOutputPort("out");
        InputPort nextInputPort = FbpTestUtils.getConnectedInputPort(outputPort);

        double fahrenheit = 102.5;
        Message message = Message.create()
                        .withEntry("temperature", fahrenheit);

        // when
        transformNode.process(message);

        Message found = nextInputPort.take();

        // then
        assertThat(found)
                .isNotNull()
                .extracting(m -> m.getPayload("temperature"))
                .isInstanceOf(Double.class)
                .isEqualTo(toCelsius(fahrenheit));
    }

    @Test
    @DisplayName("transformer가 null이면 메시지가 OutputPort로 전달되지 않는다.")
    void process_WhenTransformerNull_DoesNothing() {
        // given
        TransformNode transformNode = TransformNode.create("test", null);
        OutputPort outputPort = transformNode.getOutputPort("out");
        InputPort nextInputPort = FbpTestUtils.getConnectedInputPort(outputPort);

        Message message = Message.create();

        // when
        transformNode.onProcess(message);

        // then
        assertThat(nextInputPort.getBufferSize())
                .isZero();
    }
}