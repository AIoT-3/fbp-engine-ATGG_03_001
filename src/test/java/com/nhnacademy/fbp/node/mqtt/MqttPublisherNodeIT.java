package com.nhnacademy.fbp.node.mqtt;

import com.nhnacademy.fbp.core.message.Message;
import com.nhnacademy.fbp.core.port.InputPort;
import com.nhnacademy.fbp.core.port.OutputPort;
import com.nhnacademy.fbp.core.utils.FbpTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("integration")
class MqttPublisherNodeIT {

    static class MqttPublisherNodeFixture {
        static final String PUBLISHER_ID = UUID.randomUUID().toString();

        static MqttPublisherNode defaultPublisher() {
            MqttPublisherNode publisherNode = MqttPublisherNode.create(PUBLISHER_ID, "localhost", 1883, "test/topic");
            publisherNode.initialize();

            return publisherNode;
        }

        static MqttSubscriberNode defaultSubscriber() {
            return defaultSubscriber("test/topic");
        }

        static MqttSubscriberNode defaultSubscriber(String topic) {
            String subscriberId = UUID.randomUUID().toString();
            MqttSubscriberNode subscriberNode = MqttSubscriberNode.create(subscriberId, "localhost", 1883, topic);
            subscriberNode.initialize();

            return subscriberNode;
        }
    }

    @Test
    @DisplayName("노드를 초기화하면, 연결 상태가 CONNECTED가 된다.")
    void initialize_WhenCalled_ConnectionStateIsConnected() {
        // given
        MqttPublisherNode node = MqttPublisherNode.create(UUID.randomUUID().toString(), "test", 1234, "test");

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
        MqttPublisherNode node = MqttPublisherNodeFixture.defaultPublisher();

        // when
        node.shutdown();

        // then
        assertThat(node.isConnected())
                .isFalse();
    }

    @Test
    @DisplayName("메시지를 처리하면, JSON 형태의 MQTT 메시지가 발행된다.")
    void process_WhenCalled_PublishesMqttMessage() throws InterruptedException {
        // given
        MqttPublisherNode node = MqttPublisherNodeFixture.defaultPublisher();

        MqttSubscriberNode subscriberNode = MqttSubscriberNodeIT.MqttSubscriberNodeFixture.defaultSubscriber();
        OutputPort subscriberOutputPort = subscriberNode.getOutputPort("out");
        InputPort subscriberNextInputPort = FbpTestUtils.getConnectedInputPort(subscriberOutputPort);

        Thread subscriberThread = new Thread(subscriberNode);
        subscriberThread.start();

        Message message = Message.create()
                .withEntry("payload", "Hello, MQTT!");

        // when
        node.process(message);

        // then
        Message receivedMessage = subscriberNextInputPort.take();

        assertThat(receivedMessage)
                .isNotNull()
                .extracting(m -> m.getPayload("payload"))
                .isEqualTo("Hello, MQTT!");
    }
}