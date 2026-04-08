package com.nhnacademy.fbp.node.mqtt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.MqttGlobalPublishFilter;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;
import com.nhnacademy.fbp.common.util.JsonParserUtils;
import com.nhnacademy.fbp.core.messsage.Message;
import com.nhnacademy.fbp.core.node.ProtocolNode;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
public class MqttSubscriberNode extends ProtocolNode {
    private final Mqtt5BlockingClient client;
    private final String topic;
    private final String brokerHost;
    private final int brokerPort;

    private MqttSubscriberNode(String id, String brokerHost, int brokerPort, String topic, long reconnectIntervalMs) {
        super(id, reconnectIntervalMs);
        this.topic = topic;
        this.brokerHost = brokerHost;
        this.brokerPort = brokerPort;

        client = MqttClient.builder()
                .useMqttVersion5()
                .identifier(id)
                .serverHost(brokerHost)
                .serverPort(brokerPort)
                .buildBlocking();

        addOutputPort("out");
    }

    public static MqttSubscriberNode create(String id, String brokerHost, int brokerPort, String topic) {
        return new MqttSubscriberNode(id, brokerHost, brokerPort, topic, 5000);
    }

    @Override
    public void connect() {
        try {
            client.connect();
        } catch (Exception e) {
            log.error("MQTT 연결 실패: {}", e.getMessage(), e);
        }
    }

    @Override
    public void reconnect() {
        log.info("MQTT 연결 재시도 중...");
    }

    @Override
    public void disconnect() {
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
        try (Mqtt5BlockingClient.Mqtt5Publishes publishes = client.publishes(MqttGlobalPublishFilter.ALL)) {
            client.subscribeWith().topicFilter(topic).send();

            log.info("MQTT 구독 시작 - 브로커: {}:{}, 토픽: {}", brokerHost, brokerPort, topic);

            while (!Thread.currentThread().isInterrupted()) {
                log.info("MQTT 클라이언트 - 연결 상태: {}", client.getState());
                Mqtt5Publish publish = publishes.receive();

                String payloadStr = new String(publish.getPayloadAsBytes(), StandardCharsets.UTF_8);

                log.info("MQTT 메시지 수신 - 토픽: {}, 페이로드: {}", publish.getTopic(), payloadStr);

                Map<String, Object> payload = JsonParserUtils.get().readValue(payloadStr, new TypeReference<>() {});

                Message converted = Message.create(payload)
                        .withEntry("topic", publish.getTopic().toString());

                process(converted);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (JsonMappingException e) {
            log.error("MQTT 메시지 JSON 매핑 오류: {}", e.getMessage(), e);
        } catch (JsonProcessingException e) {
            log.error("MQTT 메시지 JSON 처리 오류: {}", e.getMessage(), e);
        } catch (Throwable e) {
            log.error("알 수 없는 MQTT 오류: {}", e.getMessage(), e);
        }
    }
}
