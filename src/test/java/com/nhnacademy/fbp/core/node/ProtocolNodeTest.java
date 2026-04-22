package com.nhnacademy.fbp.core.node;

import com.nhnacademy.fbp.core.message.Message;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProtocolNodeTest {

    static class ProtocolNodeFixture {
        static ProtocolNode defaultNode() {
            return new ProtocolNode("test") {
                @Override
                public void connect() throws Exception {
                    // 테스트용 빈 메서드
                }

                @Override
                public void disconnect() {
                    // 테스트용 빈 메서드
                }

                @Override
                protected void onProcess(Message message) {
                    // 테스트용 빈 메서드
                }
            };
        }
    }

    @Test
    @DisplayName("노드를 생성하면, 연결 상태가 DISCONNECTED로 초기화된다.")
    void create_WhenCreated_InitializesDisconnected() {
        // given & when
        ProtocolNode node = ProtocolNodeFixture.defaultNode();

        // then
        assertThat(node.getConnectionState())
                .isEqualTo(ConnectionState.DISCONNECTED);
    }

    @Test
    @DisplayName("노드를 초기화하면, 연결 상태가 CONNECTED로 변경된다.")
    void initialize_WhenInitialized_ChangesToConnected() {
        // given
        ProtocolNode node = ProtocolNodeFixture.defaultNode();

        // when
        node.initialize();

        // then
        assertThat(node.getConnectionState())
                .isEqualTo(ConnectionState.CONNECTED);
    }

     @Test
     @DisplayName("노드를 종료하면, 연결 상태가 DISCONNECTED로 변경된다.")
     void shutdown_WhenShutdown_ChangesToDisconnected() {
         // given
         ProtocolNode node = ProtocolNodeFixture.defaultNode();

         node.initialize();

         // when
         node.shutdown();

         // then
         assertThat(node.getConnectionState())
                 .isEqualTo(ConnectionState.DISCONNECTED);
     }

    @Test
    @DisplayName("연결 상태가 CONNECTED인 경우에만 isConnected()가 true를 반환한다.")
    void isConnected_WhenConnected_ReturnsTrue() {
        // given
        ProtocolNode node = ProtocolNodeFixture.defaultNode();

        // when & then
        assertThat(node.isConnected())
                .isFalse();

        node.initialize();

        assertThat(node.isConnected())
                .isTrue();

        node.shutdown();

        assertThat(node.isConnected())
                .isFalse();
    }
}