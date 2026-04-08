package com.nhnacademy.fbp.core.node;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public abstract class ProtocolNode extends AbstractNode {
    @Getter
    private ConnectionState connectionState;

    @Getter
    private final long reconnectIntervalMs;

    private final Map<String, Object> config;

    protected ProtocolNode(String id, long reconnectIntervalMs) {
        super(id);
        this.reconnectIntervalMs = reconnectIntervalMs;
        config = new HashMap<>();
        connectionState = ConnectionState.DISCONNECTED;
    }

    @Override
    public void initialize() {
        connectionState = ConnectionState.CONNECTING;

        connect();

        connectionState = ConnectionState.CONNECTED;
    }

    @Override
    public void shutdown() {
        disconnect();

        connectionState = ConnectionState.DISCONNECTED;
    }

    public abstract void connect();

    public void reconnect() {

        while (!Thread.currentThread().isInterrupted() && connectionState != ConnectionState.CONNECTED) {
            connect();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public abstract void disconnect();

    public Object getConfig(String name) {
        return config.get(name);
    }

    public boolean isConnected() {
        return this.connectionState == ConnectionState.CONNECTED;
    }
}
