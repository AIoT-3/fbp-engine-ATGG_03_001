package com.nhnacademy.fbp.core.flow;

import com.nhnacademy.fbp.core.flow.exception.FlowAlreadyExistsException;
import com.nhnacademy.fbp.core.flow.exception.FlowNotFoundException;
import com.nhnacademy.fbp.core.parser.FlowParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class FlowServiceTest {

    @Mock
    private FlowRepository flowRepository;

    @Mock
    private FlowParser flowParser;

    @InjectMocks
    private FlowService flowService;

    @Test
    @DisplayName("플로우 목록을 조회할 때, 플로우가 존재하지 않으면, 빈 리스트를 반환한다.")
    void getAllFlow_WhenFlowNotExists_ReturnsEmptyList() {
        // when
        List<Flow> found = flowService.getAllFlow();

        // then
        assertThat(found)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("이미 존재하는 ID의 플로우를 등록하려 하면, FlowAlreadyExistsException이 발생한다.")
    void register_WhenExists_ThrowsException() {
        // given
        Flow flow = Flow.create("test", "test");

        given(flowRepository.existsById(flow.getId()))
                .willReturn(true);

        // when & then
        assertThatThrownBy(() -> flowService.register(flow))
                .isInstanceOf(FlowAlreadyExistsException.class);

        then(flowRepository)
                .should(never())
                .save(any(Flow.class));
    }

    @Test
    @DisplayName("존재하지 않는 플로우를 삭제하려 하면, FlowNotFoundException이 발생한다.")
    void removeFlow_WhenNotExists_ThrowsException() {
        // given
        String id = "test";

        given(flowRepository.existsById(id))
                .willReturn(false);

        // when & then
        assertThatThrownBy(() -> flowService.removeFlow(id))
                .isInstanceOf(FlowNotFoundException.class);
    }

    @Test
    @DisplayName("존재하지 않는 플로우를 시작하려 하면, FlowNotFoundException이 발생한다.")
    void startFlow_WhenNotExists_ThrowsException() {
        // given
        String id = "test";

        given(flowRepository.findById(id))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> flowService.startFlow(id))
                .isInstanceOf(FlowNotFoundException.class);
    }

    @Test
    @DisplayName("존재하지 않는 플로우를 중지하려 하면, FlowNotFoundException이 발생한다.")
    void stopFlow_WhenNotExists_ThrowsException() {
        // given
        String id = "test";

        given(flowRepository.findById(id))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> flowService.stopFlow(id))
                .isInstanceOf(FlowNotFoundException.class);
    }
}