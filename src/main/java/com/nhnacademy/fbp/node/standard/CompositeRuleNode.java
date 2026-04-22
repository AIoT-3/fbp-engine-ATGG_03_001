package com.nhnacademy.fbp.node.standard;

import com.nhnacademy.fbp.core.message.Message;
import com.nhnacademy.fbp.core.node.AbstractNode;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class CompositeRuleNode extends AbstractNode {
    private final Operator operator;
    private final List<Predicate<Message>> conditions;

    private CompositeRuleNode(String id, Operator operator) {
        super(id);

        this.operator = operator;
        this.conditions = new ArrayList<>();

        addInputPort("in");
        addOutputPort("match");
        addOutputPort("mismatch");
    }

    public static CompositeRuleNode create(String id, Operator operator) {
        return new CompositeRuleNode(id, operator);
    }

    @Override
    protected void onProcess(Message message) {
        if (operator.evaluate(conditions, message)) {
            send("match", message);
        } else {
            send("mismatch", message);
        }
    }

    public void addCondition(Predicate<Message> condition) {
        conditions.add(condition);
    }

    public void addCondition(String expression) {
        conditions.add(RuleExpression.parse(expression));
    }
}
