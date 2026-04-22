package com.nhnacademy.fbp.core.engine;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

class NodeMetricsTest {

    @Test
    @DisplayName("record가 호출되면, 메시지 처리 횟수가 1만큼 증가하고, 총 소요 시간이 매개변수만큼 증가한다.")
    void record_WhenCalled_CountsProcessCountAndDuration() {
        // given
        NodeMetrics metrics = NodeMetrics.create();
        long processMillis = 1000;

        // when
        metrics.record(processMillis);

        // then

        assertSoftly(softly -> {
            softly.assertThat(metrics.getCount())
                    .isEqualTo(1);
            softly.assertThat(metrics.getTotalDuration())
                    .isEqualTo(processMillis);
        });
    }

    @Test
    @DisplayName("recordError가 호출되면, 에러 카운트만 1만큼 증가한다.")
    void recordError_WhenCalled_CountsOnlyErrorCount() {
        // given
        NodeMetrics metrics = NodeMetrics.create();

        // when
        metrics.recordError();

        assertSoftly(softly -> {
            softly.assertThat(metrics.getCount())
                    .isZero();
            softly.assertThat(metrics.getTotalDuration())
                    .isZero();
            softly.assertThat(metrics.getErrorCount())
                    .isEqualTo(1);
        });
    }
}