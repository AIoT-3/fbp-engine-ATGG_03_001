package com.nhnacademy.fbp.node.standard;

import com.nhnacademy.fbp.core.message.Message;
import com.nhnacademy.fbp.core.port.InputPort;
import com.nhnacademy.fbp.core.port.OutputPort;
import com.nhnacademy.fbp.core.utils.FbpTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.MAP;

class MergeNodeTest {

    @Test
    @DisplayName("'in-1'과 'in-2'에 메시지가 모두 도착하면 합쳐진 메시지가 생성된다.")
    void process_WhenReceivedMessages_CreatesMergingMessage() throws InterruptedException {
        // given
        MergeNode node = MergeNode.create("test");
        InputPort inputPort1 = node.getInputPort("in-1");
        InputPort inputPort2 = node.getInputPort("in-2");
        OutputPort outputPort = node.getOutputPort("out");

        InputPort nextInputPort = FbpTestUtils.getConnectedInputPort(outputPort);

        Thread nodeThread = new Thread(node);
        nodeThread.start();

        Message message1 = Message.create()
                .withEntry("test1", "테스트1");

        Message message2 = Message.create()
                .withEntry("test2", "테스트2");

        // when
        inputPort1.receive(message1);
        inputPort2.receive(message2);

        // then
        Message found = nextInputPort.take();

        assertThat(found)
                .extracting(Message::getPayload)
                .asInstanceOf(MAP)
                .contains(entry("test1", "테스트1"))
                .contains(entry("test2", "테스트2"));
    }

    @Test
    @DisplayName("한쪽 입력 포트에만 메시지가 도착하면 다른 쪽 입력 포트에 메시지가 도착할 때까지 대기한다.")
    void process_WhenReceiveMessageToOnePort_WaitsReceiveMessage() {
        // given
        MergeNode node = MergeNode.create("test");
        InputPort inputPort1 = node.getInputPort("in-1");
        InputPort inputPort2 = node.getInputPort("in-2");
        OutputPort outputPort = node.getOutputPort("out");

        InputPort nextInputPort = FbpTestUtils.getConnectedInputPort(outputPort);

        Thread nodeThread = new Thread(node);
        nodeThread.start();

        Message message1 = Message.create().withEntry("test1", "테스트1");
        Message message2 = Message.create().withEntry("test2", "테스트2");

        // when
        inputPort1.receive(message1);

        CompletableFuture<Message> found = CompletableFuture.supplyAsync(() -> {
            try {
                return nextInputPort.take();
            } catch (InterruptedException e) {
                return null;
            }
        });

        // then
        assertThat(found)
                .isNotCompleted();

        inputPort2.receive(message2);

        assertThat(found)
                .succeedsWithin(Duration.ofSeconds(1))
                .satisfies(message -> {
                    Map<String, Object> payload = message.getPayload();
                    assertThat(payload)
                            .containsEntry("test1", "테스트1");
                    assertThat(payload)
                            .containsEntry("test2", "테스트2");
                });
    }
}