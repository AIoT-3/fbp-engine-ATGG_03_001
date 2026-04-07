package com.nhnacademy.fbp.core.connection;

import com.nhnacademy.fbp.core.messsage.Message;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class ConnectionTest {

    @Test
    @DisplayName("deliver()로 저장된 메시지는 poll()로 꺼낼 수 있다.")
    void deliver_WhenDeliveredAndPoll_ReturnMessage() {
        // given
        Connection connection = Connection.create("test");
        Message message = Message.create();

        // when
        connection.deliver(message);

        Message found = connection.poll();

        // then
        assertThat(found)
                .isNotNull()
                .isEqualTo(message);
    }

    @Test
    @DisplayName("deliver()로 저장된 메시지는 poll()로 순서대로 꺼낼 수 있다.")
    void deliver_WhenDeliveredAndPoll_ReturnsMessagesInOrder() {
        // given
        Connection connection = Connection.create("test");

        Message message1 = Message.create();
        Message message2 = Message.create();
        Message message3 = Message.create();

        connection.deliver(message1);
        connection.deliver(message2);
        connection.deliver(message3);

        // when
        Message found1 = connection.poll();
        Message found2 = connection.poll();
        Message found3 = connection.poll();

        // then
        assertThat(found1)
                .isNotNull()
                .isEqualTo(message1);

        assertThat(found2)
                .isNotNull()
                .isEqualTo(message2);

        assertThat(found3)
                .isNotNull()
                .isEqualTo(message3);
    }

    @Test
    @DisplayName("다른 스레드에서 deliver()한 메시지는 poll()로 꺼낼 수 있다.")
    void deliver_WhenDeliveredFromAnotherThreadAndPoll_ReturnMessage() throws InterruptedException {
        // given
        Connection connection = Connection.create("test");

        Message message = Message.create();

        // when
        CountDownLatch latch = new CountDownLatch(1);

        new Thread(() -> {
            connection.deliver(message);
            latch.countDown();
        }).start();

        latch.await();

        Message found = connection.poll();

        // then
        assertThat(found)
                .isNotNull()
                .isEqualTo(message);
    }

    @Test
    @DisplayName("비어있는 버퍼에서 poll()을 호출하면 메시지가 도착할 때까지 대기한다.")
    void poll_WhenBufferIsEmpty_WaitsThread() throws InterruptedException {
        // given
        Connection connection = Connection.create("test");

        // when
        CountDownLatch latch = new CountDownLatch(1);

        new Thread(() -> {
            connection.poll();
            latch.countDown();
        }).start();

        // then
        assertThat(latch.getCount())
                .isEqualTo(1);

        connection.deliver(Message.create());

        latch.await();

        assertThat(latch.getCount())
                .isZero();
    }

    @Test
    @DisplayName("버퍼 크기를 초과하여 deliver()을 시도한 스레드는 대기한다.")
    void deliver_WhenBufferFull_WaitsThread() throws InterruptedException {
        // given
        int bufferSize = 2;
        Connection connection = Connection.create("test", bufferSize);

        connection.deliver(Message.create());
        connection.deliver(Message.create());

        // when
        CountDownLatch latch = new CountDownLatch(1);

        new Thread(() -> {
            connection.deliver(Message.create());
            latch.countDown();
        }).start();

        // then
        assertThat(latch.getCount())
                .isEqualTo(1);

        connection.poll();

        latch.await();

        assertThat(latch.getCount())
                .isZero();
    }

    @Test
    @DisplayName("getBufferSize()는 현재 버퍼에 저장된 메시지의 개수를 반환한다.")
    void getBufferSize_WhenCalled_ReturnsMessageCount() {
        // given
        Connection connection = Connection.create("test");
        int initialCount = connection.getBufferSize();

        // when
        connection.deliver(Message.create());
        int afterCount = connection.getBufferSize();

        // then
        assertSoftly(softly -> {
           softly.assertThat(initialCount)
                   .isZero();
           softly.assertThat(afterCount)
                   .isEqualTo(1);
        });
    }
}