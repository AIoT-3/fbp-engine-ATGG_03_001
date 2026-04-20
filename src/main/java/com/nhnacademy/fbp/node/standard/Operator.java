package com.nhnacademy.fbp.node.standard;

import com.nhnacademy.fbp.core.messsage.Message;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Predicate;

@RequiredArgsConstructor
public enum Operator {
    AND((conditions, message) -> conditions.stream()
                .allMatch(condition -> condition.test(message))),
    OR(((conditions, message) -> conditions.stream()
            .anyMatch(condition -> condition.test(message))));

    private final BiFunction<List<Predicate<Message>>, Message, Boolean> logicGate;

    public boolean evaluate(List<Predicate<Message>> conditions, Message message) {
        if (conditions == null || message == null) return false;

        return logicGate.apply(conditions, message);
    }
}
