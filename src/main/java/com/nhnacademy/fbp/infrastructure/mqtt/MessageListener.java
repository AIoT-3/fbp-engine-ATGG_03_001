package com.nhnacademy.fbp.infrastructure.mqtt;

public interface MessageListener {
    void onMessage(String topic, byte[] payload);
    void onConnectionLost(Throwable cause);
}
