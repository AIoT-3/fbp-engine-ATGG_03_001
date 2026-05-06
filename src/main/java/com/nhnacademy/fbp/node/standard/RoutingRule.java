package com.nhnacademy.fbp.node.standard;

import com.nhnacademy.fbp.core.message.Message;

public record RoutingRule(String field, String operator, Object value, String targetPort) {
    public boolean matches(Message message) {
        Object actual = message.getPayload().get(field);
        if (actual == null) return false;
        
        return switch (operator) {
            case "==" -> actual.equals(value);
            case "!=" -> !actual.equals(value);
            case ">" -> compare(actual, value) > 0;
            case "<" -> compare(actual, value) < 0;
            default -> false;
        };
    }

    @SuppressWarnings("unchecked")
    private int compare(Object actual, Object expected) {
        if (actual instanceof Comparable && actual.getClass().isInstance(expected)) {
            return ((Comparable<Object>) actual).compareTo(expected);
        }
        return 0;
    }
}
