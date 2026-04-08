package com.nhnacademy.fbp.core.node.impl;

import com.nhnacademy.fbp.core.messsage.Message;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DelayNodeTest {

    @Test
    @DisplayName("process() 호출 시 메시지가 지정된 지연 시간 이후에 OutputPort로 전달된다.")
    void process_WhenCalled_DelaysSending() {
        // given
        DelayNode delayNode = DelayNode.create("test", 2000);

        // when
        long startTime = System.currentTimeMillis();
        delayNode.process(Message.create());
        long endTime = System.currentTimeMillis();

        // then
        long elapsedTime = endTime - startTime;

        assertThat(elapsedTime)
                .isGreaterThanOrEqualTo(2000)
                .isLessThan(2500);
    }
}