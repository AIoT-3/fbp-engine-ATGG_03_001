package com.nhnacademy.fbp.node.mqtt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.MqttGlobalPublishFilter;
import com.hivemq.client.mqtt.lifecycle.MqttDisconnectSource;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;
import com.nhnacademy.fbp.common.util.JsonUtils;
import com.nhnacademy.fbp.core.messsage.Message;
import com.nhnacademy.fbp.core.node.ConnectionState;
import com.nhnacademy.fbp.core.node.ProtocolNode;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
public class MqttSubscriberNode extends ProtocolNode {
    private final Mqtt5BlockingClient client;
    private final String topic;
    private final String serverHost;
    private final int serverPort;

    private MqttSubscriberNode(String id, String serverHost, int serverPort, String topic) {
        super(id);
        this.topic = topic;
        this.serverHost = serverHost;
        this.serverPort = serverPort;

        client = MqttClient.builder()
                .useMqttVersion5()
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

        addOutputPort("out");
    }

    public static MqttSubscriberNode create(String id, String brokerHost, int brokerPort, String topic) {
        return new MqttSubscriberNode(id, brokerHost, brokerPort, topic);
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
    protected void disconnect() {
        try {
            client.disconnect();
        } catch (Exception e) {
            log.error("MQTT 연결 해제 실패: {}", e.getMessage(), e);
        }
    }

    @Override
    protected void onProcess(Message message) {
        send("out", message);
    }

    @Override
    public void run() {
        String jsonRaw = null;

        try (Mqtt5BlockingClient.Mqtt5Publishes publishes = client.publishes(MqttGlobalPublishFilter.ALL)) {
            client.subscribeWith().topicFilter(topic).send();

            log.info("MQTT 구독 시작 - 브로커: {}:{}, 토픽: {}", serverHost, serverPort, topic);

            while (!Thread.currentThread().isInterrupted()) {
                Mqtt5Publish publish = publishes.receive();

                jsonRaw = new String(publish.getPayloadAsBytes(), StandardCharsets.UTF_8);

                Map<String, Object> jsonMap = JsonUtils.get().readValue(jsonRaw, new TypeReference<>() {});

                Message converted = Message.from(jsonMap)
                        .withEntry("topic", publish.getTopic().toString());

                process(converted);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (JsonProcessingException e) {
            log.error("MQTT 메시지 JSON 처리 오류: {}", e.getMessage(), e);

            Message raw = Message.create()
                    .withEntry("rawPayload", jsonRaw);

            process(raw);
        } catch (Throwable e) {
            log.error("알 수 없는 MQTT 오류: {}", e.getMessage(), e);
        }
    }
}
