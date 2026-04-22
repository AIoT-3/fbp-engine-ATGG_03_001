package com.nhnacademy.fbp.node.standard;

import com.nhnacademy.fbp.core.message.Message;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class FileWriterNodeTest {

    static class FileWriterNodeFixture {
        static final String FILE_PATH = "log/test_output.txt";
    }

    @Test
    @DisplayName("초기화 메서드를 호출하면, 지정된 경로에 파일이 생성된다.")
    void initialize_WhenCalled_CreatesFile() {
        // given
        String filePath = FileWriterNodeFixture.FILE_PATH;
        FileWriterNode node = FileWriterNode.create("test", filePath);

        // when & then
        assertDoesNotThrow(node::initialize);

        File file = new File(filePath);

        assertThat(file)
                .exists();
    }

    @Test
    @DisplayName("메시지를 처리하면, 파일에 메시지가 기록된다.")
    void process_WhenCalled_WritesToFile() {
        // given
        String filePath = FileWriterNodeFixture.FILE_PATH;
        FileWriterNode node = FileWriterNode.create("test", filePath);
        node.initialize();

        Message message = Message.create()
                .withEntry("payload", "Hello, FBP!");

        // when & then
        assertDoesNotThrow(() -> node.process(message));
        assertDoesNotThrow(() -> node.process(message));
        assertDoesNotThrow(() -> node.process(message));

        File file = new File(filePath);

        assertThat(file)
                .exists()
                .hasContent(String.join(System.lineSeparator(), message.toString(), message.toString(), message.toString()) + System.lineSeparator());
    }

    @Test
    @DisplayName("종료 메서드가 호출되면, 메시지가 파일에 더 이상 기록되지 않는다.")
    void shutdown_WhenCalled_StopsWritingToFile() {
        // given
        String filePath = FileWriterNodeFixture.FILE_PATH;
        FileWriterNode node = FileWriterNode.create("test", filePath);
        node.initialize();

        Message message = Message.create()
                .withEntry("payload", "Hello, FBP!");

        // when
        node.shutdown();
        assertDoesNotThrow(() -> node.process(message));

        // then
        File file = new File(filePath);

        assertThat(file)
                .exists()
                .isEmpty();
    }
}