package com.nhnacademy.fbp.node.modbus;

import com.nhnacademy.fbp.core.messsage.Message;
import com.nhnacademy.fbp.core.node.ProtocolNode;
import com.nhnacademy.fbp.infrastructure.modbus.ModbusTcpClient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ModbusReaderNode extends ProtocolNode {
    private final int slaveId;
    private final int startAddress;
    private final int count;
    private final ModbusTcpClient client;

    private ModbusReaderNode(String id, String host, int port, int slaveId, int startAddress, int count) {
        super(id);
        this.slaveId = slaveId;
        this.startAddress = startAddress;
        this.count = count;

        client = ModbusTcpClient.create(host, port);

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

        for (int i : data) {
            result = result.withEntry("test", i);
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
