package com.nhnacademy.fbp.core.node;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class ProtocolNode extends AbstractNode {
    @Getter
    private volatile ConnectionState connectionState;

    @Getter
    private final long reconnectIntervalMs;

    protected ProtocolNode(String id, long reconnectIntervalMs) {
        super(id);
        this.reconnectIntervalMs = reconnectIntervalMs;
        connectionState = ConnectionState.DISCONNECTED;
    }

    protected ProtocolNode(String id) {
        this(id, 5000);
    }

    @Override
    public void initialize() {
        connectionState = ConnectionState.CONNECTING;

        try {
            connect();

            connectionState = ConnectionState.CONNECTED;
        } catch (Exception e) {
            log.error("연결 실패: {}", e.getMessage(), e);
        }
    }

    @Override
    public void shutdown() {
        try {
            disconnect();

            connectionState = ConnectionState.DISCONNECTED;
        } catch (Exception e) {
            log.error("연결 해제 실패: {}", e.getMessage(), e);
        }
    }

    protected abstract void connect() throws Exception;

    protected void reconnect() {
        while (connectionState != ConnectionState.CONNECTED) {
            log.info("연결 재시도 중...");

            try {
                Thread.sleep(reconnectIntervalMs);

                connect();

                connectionState = ConnectionState.CONNECTED;

                log.info("연결 재시도 성공");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                log.error("연결 재시도 실패: {}", e.getMessage(), e);
            }
        }
    }

    protected abstract void disconnect() throws Exception;

    public boolean isConnected() {
        return this.connectionState == ConnectionState.CONNECTED;
    }

    protected void setConnectionState(ConnectionState state) {
        this.connectionState = state;
    }
}
