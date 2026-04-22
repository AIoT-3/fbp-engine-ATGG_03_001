package com.nhnacademy.fbp.node.standard;

import com.nhnacademy.fbp.core.message.Message;
import com.nhnacademy.fbp.core.node.AbstractNode;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Predicate;

public class TimeWindowRuleNode extends AbstractNode {
    private final Predicate<Message> condition;
    private final long windowMs;
    private final int threshold;
    private final Deque<Long> events;

    private TimeWindowRuleNode(String id, String expression, long windowMs, int threshold) {
        super(id);
        condition = RuleExpression.parse(expression);
        this.windowMs = windowMs;
        this.threshold = threshold;
        events = new ArrayDeque<>();

        addInputPort("in");
        addOutputPort("alert");
        addOutputPort("pass");
    }

    public static TimeWindowRuleNode create(String id, String expression, long windowMs, int threshold) {
        return new TimeWindowRuleNode(id, expression, windowMs, threshold);
    }

    @Override
    protected void onProcess(Message message) {
        long currentTime = System.currentTimeMillis();

        if (condition.test(message)) {
            events.addLast(currentTime);

            long oldTime = currentTime - windowMs;

            while (!events.isEmpty() && events.peekFirst() < oldTime) {
                events.removeFirst();
            }

            if (events.size() > threshold) {
                send("alert", message);
            } else {
                send("pass", message);
            }
        }
    }

    public int getWindowSize() {
        return events.size();
    }
}
