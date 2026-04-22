package com.nhnacademy.fbp.core.message;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Message {
    private final UUID id;
    private final Map<String, Object> payload;
    private final long timestamp;

    public static Message create() {
        return create(new HashMap<>());
    }

    public static Message create(Map<String, Object> payload) {
        return new Message(
                UUID.randomUUID(),
                Map.copyOf(payload),
                System.currentTimeMillis()
        );
    }

    public static Message from(Map<String, Object> jsonMap) {
        if (!jsonMap.containsKey("id") || !jsonMap.containsKey("timestamp") || !jsonMap.containsKey("payload")) {
            return create(jsonMap);
        }

        UUID id = UUID.fromString(String.valueOf(jsonMap.get("id")));
        Number timestamp = (Number) jsonMap.get("timestamp");
        Map<String, Object> payload = (Map<String, Object>) jsonMap.get("payload");

        return new Message(
                id,
                Map.copyOf(payload),
                timestamp.longValue()
        );
    }

    public Message withEntry(String key, Object value) {
        Map<String, Object> newPayload = new HashMap<>(payload);

        newPayload.put(key, value);

        return new Message(id, Collections.unmodifiableMap(newPayload), timestamp);
    }

    public Message withoutKey(String key) {
        Map<String, Object> newPayload = new HashMap<>(payload);

        newPayload.remove(key);

        return new Message(id, Collections.unmodifiableMap(newPayload), timestamp);
    }

    public boolean hasKey(String key) {
        return payload.containsKey(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T getPayload(String key) {
        return (T) payload.get(key);
    }

    @Override
    public String toString() {
        String payloadStr = payload.entrySet().stream()
                .map(entry -> String.format("%s=%s", entry.getKey(), entry.getValue()))
                .collect(Collectors.joining(", "));

        return String.format("[%s] { %s }", id, payloadStr);
    }
}
