package com.nhnacademy.fbp.core.node;

import com.nhnacademy.fbp.core.message.Message;

public interface Node {
    String getId();
    void process(Message message);
    default void initialize() {}
    default void shutdown() {}
}
