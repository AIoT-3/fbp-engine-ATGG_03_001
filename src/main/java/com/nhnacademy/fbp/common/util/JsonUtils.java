package com.nhnacademy.fbp.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JsonUtils {
    private static class Holder {
        private static final ObjectMapper INSTANCE = new ObjectMapper();
    }

    public static ObjectMapper get() {
        return Holder.INSTANCE;
    }
}
