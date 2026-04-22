package com.nhnacademy.fbp.node.standard;

import com.nhnacademy.fbp.core.message.Message;
import com.nhnacademy.fbp.core.node.AbstractNode;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public class LogNode extends AbstractNode {
    private final DateTimeFormatter formatter;

    private LogNode(String id) {
        super(id);
        formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

        addInputPort("in");
        addOutputPort("out");
    }

    public static LogNode create(String id) {
        return new LogNode(id);
    }

    @Override
    protected void onProcess(Message message) {
        log.info("[{}]{}", formatter.format(LocalDateTime.now()), message);

        send("out", message);
    }
}
