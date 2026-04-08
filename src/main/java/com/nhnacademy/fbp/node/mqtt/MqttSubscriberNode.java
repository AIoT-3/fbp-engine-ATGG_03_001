package com.nhnacademy.fbp.node.mqtt;

import com.fasterxml.jackson.core.type.TypeReference;
import com.nhnacademy.fbp.common.util.JsonParserUtils;
import com.nhnacademy.fbp.core.messsage.Message;
import com.nhnacademy.fbp.core.node.ProtocolNode;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttAsyncClient;
import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.common.MqttException;

import java.util.Map;

@Slf4j
public class MqttSubscriberNode extends ProtocolNode {
    private final MqttClient client;
    private final String topic;
    private final MqttConnectionOptions options;

    private MqttSubscriberNode(String id, String brokerUrl, String topic, long reconnectIntervalMs) throws MqttException {
        super(id, reconnectIntervalMs);
        this.topic = topic;
        client = new MqttClient(brokerUrl, id);

        options = new MqttConnectionOptions();
        options.setConnectionTimeout(5);
        options.setCleanStart(true);
        options.setAutomaticReconnect(true);

        addOutputPort("out");
    }

    public static MqttSubscriberNode create(String id, String brokerUrl, String topic) throws MqttException {
        return new MqttSubscriberNode(id, brokerUrl, topic, 5000);
    }

    @Override
    public void connect() {
        try {
            client.connect(options);

            log.info("연결 여부: {}", client.isConnected());
        } catch (MqttException e) {

            log.info("{}", client.getDebug());
            log.info("MQTT 연결 중 오류 발생: {}", e.getMessage(), e);
            reconnect();
        }
    }

    @Override
    public void reconnect() {
        log.info("MQTT 연결 재시도 중...");
    }

    @Override
    public void disconnect() {
        try {
            client.close();
        } catch (MqttException e) {
            log.error("MQTT 연결 종료 중 오류 발생: {}", e.getMessage());
        }
    }

    @Override
    protected void onProcess(Message message) {
        send("out", message);
    }

    @Override
    public void run() {
        try {
            log.info("클라이언트 상태: {}", client != null);
            client.subscribe(topic, 1, (t, message) -> {
                try {
                    log.info("MQTT 콜백 실행: {}", topic);

                    String payloadStr = new String(message.getPayload());

                    log.info("MQTT 메시지 도착: {}", payloadStr);

                    Map<String, Object> payload = JsonParserUtils.get().readValue(payloadStr, new TypeReference<>() {
                    });

                    Message converted = Message.create(payload)
                            .withEntry("topic", t);

                    process(converted);
                } catch (Exception e) {
                    log.error("에러 발생: {}", e.getMessage(), e);
                }
            });

//            IMqttToken token = client.subscribe(topic, 1, (t, msg) -> {
//                log.info("실행");
//            });

//            token.waitForCompletion(5000);

            System.out.println("test");


            while (!Thread.currentThread().isInterrupted()) {
                Thread.sleep(10000);
            }

        } catch (MqttException e) {
            log.error("MQTT 실행 오류 발생: {}", e.getMessage());
            reconnect();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.info("{}", e.getMessage(), e);
        } finally {
            System.out.println("test");
        }

    }
}
