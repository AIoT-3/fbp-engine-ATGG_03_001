package com.nhnacademy.fbp.benchmark;

import com.nhnacademy.fbp.core.flow.Flow;
import com.nhnacademy.fbp.core.message.Message;
import com.nhnacademy.fbp.core.node.AbstractNode;
import com.nhnacademy.fbp.node.mqtt.MqttPublisherNode;
import com.nhnacademy.fbp.node.mqtt.MqttSubscriberNode;
import com.nhnacademy.fbp.node.standard.TemperatureSensorNode;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 3, time = 5)
@Measurement(iterations = 5, time = 10)
@Fork(1)
public class FbpEngineBenchmark {

    private Flow flow;
    private TriggerNode triggerNode;

    @Setup
    public void setup() {
        flow = Flow.create("benchmark-mqtt", "MQTT Benchmark Flow");

        triggerNode = TriggerNode.create("trigger");
        TemperatureSensorNode temperatureSensorNode = TemperatureSensorNode.create("temperature", 20, 40);
        MqttPublisherNode pubNode = MqttPublisherNode.create("pub", "localhost", 1883, "test/bench", 0);
        MqttSubscriberNode subNode = MqttSubscriberNode.create("sub", "localhost", 1883, "test/bench");

        flow.addNode(triggerNode)
                .addNode(temperatureSensorNode)
                .addNode(pubNode)
                .addNode(subNode)
                .connect("trigger", "out", "temperature", "trigger")
                .connect("temperature", "pub");
        
        flow.initialize();
    }

    @TearDown
    public void tearDown() {
        flow.shutdown();
    }

    @Benchmark
    public void testMqttFlowThroughput() {
        triggerNode.process(Message.create());
    }

    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(FbpEngineBenchmark.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }

    private static class TriggerNode extends AbstractNode {
        private TriggerNode(String id) {
            super(id);
            addOutputPort("out");
        }

        @Override
        protected void onProcess(Message message) {
            send("out", message);
        }

        public static TriggerNode create(String id) {
            return new TriggerNode(id);
        }
    }
}
