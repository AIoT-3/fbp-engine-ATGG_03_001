package com.nhnacademy.fbp.node.standard;

import com.nhnacademy.fbp.core.messsage.Message;
import com.nhnacademy.fbp.core.node.AbstractNode;

import java.util.function.Predicate;

public class RuleNode extends AbstractNode {
    private final Predicate<Message> condition;

    private RuleNode(String id, String expression) {
        super(id);

        condition = RuleExpression.parse(expression);

        addInputPort("in");
        addOutputPort("match");
        addOutputPort("mismatch");
    }

    public static RuleNode create(String id, String expression) {
        return new RuleNode(id, expression);
    }

    @Override
    protected void onProcess(Message message) {

        if (condition.test(message)) {
            send("match", message);
        } else {
            send("mismatch", message);
        }
    }
}
