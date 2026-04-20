package com.nhnacademy.fbp.node.modbus;

import com.nhnacademy.fbp.core.messsage.Message;
import com.nhnacademy.fbp.core.node.ProtocolNode;
import com.nhnacademy.fbp.infrastructure.modbus.ModbusTcpClient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ModbusWriterNode extends ProtocolNode {
    private final int slaveId;
    private final int registerAddress;
    private final String valueField;
    private final double scale;
    private final ModbusTcpClient client;

    private ModbusWriterNode(String id, String host, int port, int slaveId, int registerAddress, String valueField, double scale) {
        super(id);
        this.slaveId = slaveId;
        this.registerAddress = registerAddress;
        this.valueField = valueField;
        this.scale = scale;

        addInputPort("in");

        client = ModbusTcpClient.create(host, port);
    }

    public static ModbusWriterNode create(String id, String host, int port, int slaveId, int registerAddress, String valueField, double scale) {
        return new ModbusWriterNode(id, host, port, slaveId, registerAddress, valueField, scale);
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
        Number value = message.getPayload(valueField);

        int scaledValue = (int) (value.doubleValue() * scale);

        client.writeSingleRegister(slaveId, registerAddress, scaledValue);
    }
}
