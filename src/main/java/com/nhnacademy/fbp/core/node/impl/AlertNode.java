package com.nhnacademy.fbp.core.node.impl;


import com.nhnacademy.fbp.core.messsage.Message;
import com.nhnacademy.fbp.core.node.AbstractNode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AlertNode extends AbstractNode {
    private static final String SENSOR_KEY = "sensorId";
    private static final String TARGET_KEY = "temperature";

    private AlertNode(String id) {
        super(id);

        addInputPort("in");
    }

    public static AlertNode create(String id) {
        return new AlertNode(id);
    }

    @Override
    protected void onProcess(Message message) {
        if (!message.hasKey(TARGET_KEY)) {
            log.info("[경고] 알 수 없는 센서 데이터");
            return;
        }

        String sensorId = message.getPayload(SENSOR_KEY);
        double temperature = message.getPayload(TARGET_KEY);

        log.info("[경고] 센서 {} 온도 {}°C - 임계값 초과", sensorId, temperature);
    }
}
