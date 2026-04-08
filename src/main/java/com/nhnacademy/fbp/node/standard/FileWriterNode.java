package com.nhnacademy.fbp.node.standard;

import com.nhnacademy.fbp.core.messsage.Message;
import com.nhnacademy.fbp.core.node.AbstractNode;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

@Slf4j
public class FileWriterNode extends AbstractNode {
    private final String filePath;
    private BufferedWriter writer;

    private FileWriterNode(String id, String filePath) {
        super(id);
        this.filePath = filePath;

        addInputPort("in");
    }

    public static FileWriterNode create(String id, String filePath) {
        return new FileWriterNode(id, filePath);
    }

    @Override
    public void initialize() {
        try {
            writer = new BufferedWriter(new FileWriter(filePath));
        } catch (IOException e) {
            log.error("파일 열기 실패: {}", e.getMessage());
        }
    }

    @Override
    protected void onProcess(Message message) {
        try {
            writer.write(message.toString());
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            log.error("파일 쓰기 실패: {}", e.getMessage());
        }
    }

    @Override
    public void shutdown() {
        if (writer == null) return;

        try {
            writer.close();
        } catch (IOException e) {
            log.error("파일 닫기 실패: {}", e.getMessage());
        }
    }
}
