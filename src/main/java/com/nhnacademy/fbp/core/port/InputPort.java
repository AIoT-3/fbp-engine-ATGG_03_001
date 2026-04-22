package com.nhnacademy.fbp.core.port;

import com.nhnacademy.fbp.core.message.Message;
import com.nhnacademy.fbp.core.node.AbstractNode;

public interface InputPort {
    void receive(Message message);
    String getName();
    AbstractNode getOwner();
    Message take() throws InterruptedException;
    int getBufferSize();
}
