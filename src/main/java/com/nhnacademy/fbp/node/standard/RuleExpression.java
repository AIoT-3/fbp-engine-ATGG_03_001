package com.nhnacademy.fbp.node.standard;

import com.nhnacademy.fbp.core.message.Message;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.function.Predicate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RuleExpression {
    public static Predicate<Message> parse(String expression) {
        String[] parts = expression.split("\\s+");

        String field = parts[0].trim();
        String operator = parts[1].trim();
        double value = Double.parseDouble(parts[2].trim());

        return (message -> {
            Number valueObj = message.getPayload(field);

            if (valueObj == null) return false;

            double messageValue = valueObj.doubleValue();

            return evaluate(messageValue, operator, value);
        });
    }

    private static boolean evaluate(double operand1, String operator, double operand2) {
        switch (operator) {
            case ">" -> {
                return operand1 > operand2;
            }
            case ">=" -> {
                return operand1 >= operand2;
            }
            case "<" -> {
                return operand1 < operand2;
            }
            case "<=" -> {
                return operand1 <= operand2;
            }
            case "==" -> {
                return operand1 == operand2;
            }
            case "!=" -> {
                return operand1 != operand2;
            }
            default -> {
                return false;
            }
        }
    }


}
