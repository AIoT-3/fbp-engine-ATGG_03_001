package com.nhnacademy.fbp.node.mqtt;

import com.nhnacademy.fbp.core.messsage.Message;
import com.nhnacademy.fbp.core.port.InputPort;
import com.nhnacademy.fbp.core.port.OutputPort;
import com.nhnacademy.fbp.core.utils.FbpTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.MAP;

@Tag("integration")
class MqttSubscriberNodeIT {

    static class MqttSubscriberNodeFixture {
        static MqttPublisherNode defaultPublisher() {
            String publisherId = UUID.randomUUID().toString();
            MqttPublisherNode publisherNode = MqttPublisherNode.create(publisherId, "localhost", 1883, "test/topic");
            publisherNode.initialize();

            return publisherNode;
        }

        static MqttSubscriberNode defaultSubscriber() {
            String subscriberId = UUID.randomUUID().toString();
            MqttSubscriberNode subscriberNode = MqttSubscriberNode.create(subscriberId, "localhost", 1883, "test/topic");
            subscriberNode.initialize();

            return subscriberNode;
        }
    }

    @Test
    @DisplayName("노드를 초기화하면, 연결 상태가 CONNECTED가 된다.")
    void initialize_WhenCalled_ConnectionStateIsConnected() {
        // given
        String id = UUID.randomUUID().toString();
        MqttSubscriberNode node = MqttSubscriberNode.create(id, "localhost", 1883, "test/topic");

        // when
        node.initialize();

        // then
        assertThat(node.isConnected())
                .isTrue();
    }

    @Test
    @DisplayName("노드를 종료하면, 연결 상태가 DISCONNECTED가 된다.")
    void shutdown_WhenCalled_ConnectionStateIsDisconnected() {
        // given
        MqttSubscriberNode node = MqttSubscriberNodeIT.MqttSubscriberNodeFixture.defaultSubscriber();

        // when
        node.initialize();
        node.shutdown();

        // then
        assertThat(node.isConnected())
                .isFalse();
    }

    @Test
    @DisplayName("JSON 형식의 MQTT 메시지를 수신하면, Message 객체로 변환되어 'out' 포트로 전달된다.")
    void run_WhenReceiveMqttMessage_ConvertsMessage() throws InterruptedException {
        // given
        MqttSubscriberNode node = MqttSubscriberNodeFixture.defaultSubscriber();
        OutputPort outputPort = node.getOutputPort("out");
        InputPort nextInputPort = FbpTestUtils.getConnectedInputPort(outputPort);

        MqttPublisherNode publisherNode = MqttSubscriberNodeFixture.defaultPublisher();

        Thread mqttSubscribeThread = new Thread(node);
        mqttSubscribeThread.start();

        Map.Entry<String, Object> entry = Map.entry("message", "Hello, MQTT!");
        Map<String, Object> payload = Map.of(entry.getKey(), entry.getValue());

        Message expected = Message.create()
                .withEntry("payload", payload);

        // when
        publisherNode.process(expected);

        // then
        Message found = nextInputPort.take();

        assertThat(found)
                .isNotNull()
                .extracting(Message::getId)
                .isEqualTo(expected.getId());

        assertThat(found)
                .extracting(Message::getPayload)
                .asInstanceOf(MAP)
                .containsEntry("payload", payload);
    }

    @Test
    @DisplayName("잘못된 JSON 형식의 MQTT 메시지를 수신하면, 변환된 Message 객체의 'rawJson' 키에 원본 JSON 문자열이 포함된다.")
    void run_WhenReceiveInvalidMqttMessage_IncludesRawJson() throws InterruptedException {
        // given
        MqttSubscriberNode node = MqttSubscriberNodeFixture.defaultSubscriber();
        OutputPort outputPort = node.getOutputPort("out");
        InputPort nextInputPort = FbpTestUtils.getConnectedInputPort(outputPort);

        MqttPublisherNode publisherNode = MqttSubscriberNodeFixture.defaultPublisher();

        Thread mqttSubscribeThread = new Thread(node);
        mqttSubscribeThread.start();

        String invalidJson = "{message: Hello, MQTT!}";

        Message expected = Message.create()
                .withEntry("rawJson", invalidJson);

        // when
        publisherNode.process(expected);

        // then
        Message found = nextInputPort.take();

        assertThat(found)
                .isNotNull()
                .extracting(Message::getPayload)
                .asInstanceOf(MAP)
                .containsEntry("rawJson", invalidJson);
    }
}