package com.nhnacademy.fbp.core.port;

import com.nhnacademy.fbp.core.messsage.Message;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultInputPortTest {

    @Test
    @DisplayName("메시지를 수신하면 내부의 버퍼에 메시지가 저장된다.")
    void receive_WhenMessageReceived_StoresInQueue() throws InterruptedException {
        // given
        DefaultInputPort inputPort = DefaultInputPort.create("test", null);
        Message message = Message.create();

        // when
        inputPort.receive(message);

        // then
        Message found = inputPort.take();
        assertThat(found)
                .isNotNull()
                .isEqualTo(message);
    }

    @Test
    @DisplayName("다른 스레드에서 메시지를 수신한 메시지를 take()로 꺼낼 수 있다.")
    void receive_WhenMessageReceivedFromAnotherThread_takeReturnsMessage() throws InterruptedException {
        // given
        DefaultInputPort inputPort = DefaultInputPort.create("test", null);
        Message message = Message.create();

        // when
        Thread receiverThread = new Thread(() -> {
            inputPort.receive(message);
        });

        receiverThread.start();
        receiverThread.join();

        // then
        Message found = inputPort.take();
        assertThat(found)
                .isNotNull()
                .isEqualTo(message);
    }

    @Test
    @DisplayName("비어 있는 버퍼에서 take()을 호출한 스레드는 메시지가 수신될 때까지 대기한다.")
    void take_WhenBufferEmpty_WaitsThreads() throws InterruptedException {
        // given
        DefaultInputPort inputPort = DefaultInputPort.create("test", null);
        Message message = Message.create();

        // when
        CountDownLatch latch = new CountDownLatch(1);

        new Thread(() -> {
            try {
                inputPort.take();
                latch.countDown();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();

        // then
        assertThat(latch.getCount())
                .isEqualTo(1);

        inputPort.receive(message);

        latch.await();

        assertThat(latch.getCount())
                .isZero();
    }

    @Test
    @DisplayName("getBufferSize()를 호출하면 현재 버퍼에 저장된 메시지의 개수를 반환한다.")
    void getBufferSize_WhenCalled_ReturnsCurrentBufferSize() {
        // given
        DefaultInputPort inputPort = DefaultInputPort.create("test", null);
        Message message1 = Message.create();
        Message message2 = Message.create();

        // when & then
        assertThat(inputPort.getBufferSize())
                .isEqualTo(0);

        inputPort.receive(message1);

        assertThat(inputPort.getBufferSize())
                .isEqualTo(1);

        inputPort.receive(message2);

        assertThat(inputPort.getBufferSize())
                .isEqualTo(2);
    }
}