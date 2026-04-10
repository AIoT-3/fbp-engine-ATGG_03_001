package com.nhnacademy.fbp.node.mqtt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.lifecycle.MqttDisconnectSource;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;
import com.nhnacademy.fbp.common.util.JsonUtils;
import com.nhnacademy.fbp.core.messsage.Message;
import com.nhnacademy.fbp.core.node.ConnectionState;
import com.nhnacademy.fbp.core.node.ProtocolNode;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Slf4j
public class MqttPublisherNode extends ProtocolNode {
    private final String topic;
    private final MqttQos qos;
    private final Mqtt5BlockingClient client;

    private MqttPublisherNode(String id, String serverHost, int serverPort, String topic, int qos) {
        super(id);
        this.topic = topic;
        this.qos = MqttQos.fromCode(qos);

        addInputPort("in");

        client = Mqtt5Client.builder()
                .identifier(id)
                .serverHost(serverHost)
                .serverPort(serverPort)
                .addConnectedListener(context -> setConnectionState(ConnectionState.CONNECTED))
                .addDisconnectedListener(context -> {
                    setConnectionState(ConnectionState.DISCONNECTED);

                    context.getReconnector()
                            .reconnect(context.getSource() != MqttDisconnectSource.USER)
                            .delay(getReconnectIntervalMs(), TimeUnit.MILLISECONDS);
                })
                .buildBlocking();
    }

    public static MqttPublisherNode create(String id, String serverHost, int serverPort, String topic, int qos) {
        return new MqttPublisherNode(id, serverHost, serverPort, topic, qos);
    }

    public static MqttPublisherNode create(String id, String serverHost, int serverPort, String topic) {
        return create(id, serverHost, serverPort, topic, 1);
    }

    @Override
    protected void connect() throws Exception {
        client.connect();
    }

    @Override
    protected void reconnect() {
        // 자동 재연결 설정 사용
    }

    @Override
    protected void disconnect() throws Exception {
        client.disconnect();
    }

    @Override
    protected void onProcess(Message message) {
        try {
            String msgTopic = message.getPayload("topic");

            String json = JsonUtils.get().writeValueAsString(message);

            Mqtt5Publish publish = Mqtt5Publish.builder()
                    .topic((msgTopic == null) ? topic : msgTopic)
                    .payload(json.getBytes(StandardCharsets.UTF_8))
                    .qos(qos)
                    .build();

            client.publish(publish);
        } catch (JsonProcessingException e) {
            log.error("Message JSON 처리 실패: {}", e.getMessage(), e);
        }
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Message message = takeMessage("in");

                process(message);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
