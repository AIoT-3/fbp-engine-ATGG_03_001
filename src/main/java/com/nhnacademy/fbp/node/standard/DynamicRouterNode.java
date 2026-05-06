package com.nhnacademy.fbp.node.standard;

import com.nhnacademy.fbp.core.message.Message;
import com.nhnacademy.fbp.core.node.AbstractNode;

import java.util.ArrayList;
import java.util.List;

public class DynamicRouterNode extends AbstractNode {
    private final List<RoutingRule> rules;

    private DynamicRouterNode(String id, List<RoutingRule> rules) {
        super(id);
        this.rules = new ArrayList<>(rules);
        
        addInputPort("in");
        addOutputPort("default");
        
        // 규칙에 정의된 모든 타겟 포트를 미리 생성하여 정적 구조 유지
        rules.forEach(rule -> {
            if (!getOutputPorts().containsKey(rule.targetPort())) {
                addOutputPort(rule.targetPort());
            }
        });
    }

    public static DynamicRouterNode create(String id, List<RoutingRule> rules) {
        return new DynamicRouterNode(id, rules);
    }

    @Override
    protected void onProcess(Message message) {
        for (RoutingRule rule : rules) {
            if (rule.matches(message)) {
                send(rule.targetPort(), message);
                return;
            }
        }
        send("default", message);
    }
}
