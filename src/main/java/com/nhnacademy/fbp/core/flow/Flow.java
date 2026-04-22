package com.nhnacademy.fbp.core.flow;

import com.nhnacademy.fbp.core.connection.Connection;
import com.nhnacademy.fbp.core.engine.MetricCollector;
import com.nhnacademy.fbp.core.flow.exception.CircularConnectionException;
import com.nhnacademy.fbp.core.flow.exception.FlowValidationException;
import com.nhnacademy.fbp.core.node.AbstractNode;
import com.nhnacademy.fbp.core.node.exception.NodeNotFoundException;
import com.nhnacademy.fbp.core.port.InputPort;
import com.nhnacademy.fbp.core.port.OutputPort;
import com.nhnacademy.fbp.core.port.exception.InputPortNotFoundException;
import com.nhnacademy.fbp.core.port.exception.OutputPortNotFoundException;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
public class Flow {
    private static final String DEFAULT_INPUT_PORT = "in";
    private static final String DEFAULT_OUTPUT_PORT = "out";

    private final String id;
    private String name;
    private final Map<String, AbstractNode> nodes;
    private final List<Connection> connections;
    private ExecutorService executorService;
    private FlowState state;

    private Flow(String id, String name) {
        this.id = id;
        this.name = name;
        state = FlowState.STOPPED;

        nodes = new HashMap<>();
        connections = new ArrayList<>();
        executorService = Executors.newVirtualThreadPerTaskExecutor();
    }

    public static Flow create(String id, String name) {
        return new Flow(id, name);
    }

    public Flow addNode(AbstractNode node) {
        nodes.put(node.getId(), node);

        return this;
    }

    public Flow connect(String srcId, String srcPort, String tgtId, String tgtPort) {
        AbstractNode source = getNode(srcId);
        AbstractNode target = getNode(tgtId);

        OutputPort sourceOutput = source.getOutputPort(srcPort);

        if (sourceOutput == null) {
            throw new OutputPortNotFoundException();
        }

        InputPort targetInput = target.getInputPort(tgtPort);

        if (targetInput == null) {
            throw new InputPortNotFoundException();
        }

        String connectionId = String.format("%s:%s->%s:%s", srcId, srcPort, tgtId, tgtPort);
        Connection connection = Connection.create(connectionId);

        sourceOutput.connect(connection);
        connection.setTarget(targetInput);

        connections.add(connection);

        return this;
    }

    public Flow connect(String srcId, String tgtId) {
        return connect(srcId, DEFAULT_OUTPUT_PORT, tgtId, DEFAULT_INPUT_PORT);
    }

    public void initialize() {
        nodes.values().forEach(AbstractNode::initialize);

        if (executorService.isShutdown()) {
            executorService = Executors.newVirtualThreadPerTaskExecutor();
        }

        nodes.values().forEach(executorService::submit);

        state = FlowState.RUNNING;
    }

    public void shutdown() {
        nodes.values().forEach(AbstractNode::shutdown);

        executorService.shutdown();

        state = FlowState.STOPPED;
    }

    public void validate() {
        if (nodes.isEmpty()) {
            throw new FlowValidationException();
        }

        try {
            nodes.values()
                    .forEach(node -> checkCircularDependency(node, new HashSet<>()));
        } catch (CircularConnectionException e) {
            throw new FlowValidationException();
        }
    }

    public void setupMonitoring(MetricCollector metricCollector) {
        nodes.values().forEach(node -> node.setupMonitoring(metricCollector));
    }

    private void checkCircularDependency(AbstractNode node, Set<AbstractNode> visited) {
        if (visited.contains(node)) {
            throw new CircularConnectionException();
        }

        visited.add(node);

        node.getOutputPorts().values().stream()
                .map(OutputPort::getConnections)
                .flatMap(List::stream)
                .map(Connection::getTarget)
                .forEach(target -> checkCircularDependency(target.getOwner(), visited));
    }

    private AbstractNode getNode(String id) {
        if (!nodes.containsKey(id)) {
            throw new NodeNotFoundException();
        }

        return nodes.get(id);
    }
}
