package com.nhnacademy.fbp.node.standard;

import com.nhnacademy.fbp.core.messsage.Message;

import java.util.List;
import java.util.function.Predicate;

@FunctionalInterface
public interface LogicGate {
    boolean apply(List<Predicate<Message>> condition, Message message);
}
