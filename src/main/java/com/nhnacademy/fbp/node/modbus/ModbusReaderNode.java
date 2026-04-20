package com.nhnacademy.fbp.node.modbus;

import com.nhnacademy.fbp.core.messsage.Message;
import com.nhnacademy.fbp.core.node.ProtocolNode;
import com.nhnacademy.fbp.infrastructure.modbus.ModbusTcpClient;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ModbusReaderNode extends ProtocolNode {
    private final int slaveId;
    private final int startAddress;
    private final int count;
    private final ModbusTcpClient client;
    private final Map<Integer, Map<String, Object>> registerMapping;

    private ModbusReaderNode(String id, String host, int port, int slaveId, int startAddress, int count) {
        super(id);
        this.slaveId = slaveId;
        this.startAddress = startAddress;
        this.count = count;

        client = ModbusTcpClient.create(host, port);

        registerMapping = new HashMap<>();
        registerMapping.put(0, Map.of("name", "temperature", "scale", 0.1));
        registerMapping.put(1, Map.of("name", "humidity", "scale", 0.1));
        registerMapping.put(2, Map.of("name", "status"));

        addInputPort("trigger");
        addOutputPort("out");
        addOutputPort("error");
    }

    public static ModbusReaderNode create(String id, String host, int port, int slaveId, int startAddress, int count) {
        return new ModbusReaderNode(id, host, port, slaveId, startAddress, count);
    }

    @Override
    protected void connect() throws Exception {
        client.connect();
    }

    @Override
    protected void disconnect() throws Exception {
        client.disconnect();
    }

    @Override
    protected void onProcess(Message message) {
        int[] data = client.readHoldingRegisters(slaveId, startAddress, count);
        Message result = Message.create();

        String entryName = String.valueOf(registerMapping.get(slaveId).get("name"));
        Number scale = (Number) registerMapping.get(slaveId).get("scale");

        if (scale == null) {
            scale = 1;
        }

        for (int i : data) {
            String scaledValue = String.format("%.1f", i * scale.doubleValue());
            result = result.withEntry(entryName, Double.parseDouble(scaledValue));
        }

        send("out", result);
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Message message = takeMessage("trigger");

                process(message);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

        }
    }
}
