package com.nhnacademy.fbp.core.message;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MessageTest {

    static class MessageFixture {
        static final String KEY = "testKey";
        static final String VALUE = "testValue";
        static Message defaultMessage() {
            return Message.create()
                    .withEntry(KEY, VALUE);
        }
    }

    @Test
    @DisplayName("메시지 생성 시 ID가 자동으로 할당된다.")
    void create_WhenMessageCreate_AutoAssignId() {
        // given & when
        Message message = Message.create();

        // then
        assertThat(message.getId())
                .isNotNull();
    }

    @Test
    @DisplayName("메시지 생성 시 타임스탬프가 자동으로 기록된다.")
    void create_WhenMessageCreate_AutoAssignTimestamp() {
        // given & when
        Message message = Message.create();

        // then
        assertThat(message.getTimestamp())
                .isGreaterThan(0L);
    }

    @Test
    @DisplayName("key-value를 getPayload()으로 꺼낼 수 있다.")
    void getPayload_WhenExists_ReturnValue() {
        // given
        Message message = MessageFixture.defaultMessage();

        // when
        String found = message.getPayload(MessageFixture.KEY);

        // then
        assertThat(found)
                .isNotNull()
                .isNotEmpty()
                .isEqualTo(MessageFixture.VALUE);
    }

    @Test
    @DisplayName("존재하지 않는 key를 조회하면 null을 반환한다.")
    void getPayload_WhenNotExists_ReturnNull() {
        // given
        String key = "invalidKey";

        Message message = Message.create();

        // when
        String value = message.getPayload(key);

        assertThat(value)
                .isNull();
    }

    @Test
    @DisplayName("페이로드를 외부에서 직접 수정하려 하면 UnsupportedOperationException이 발생한다.")
    void getPayload_WhenModifyExternal_ThrowsException() {
        // given
        Message message = MessageFixture.defaultMessage();

        Map<String, Object> payload = message.getPayload();

        // when & then
        assertThatThrownBy(() -> payload.put("newKey", "newValue"))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("메시지 생성에 사용된 페이로드를 수정해도 메시지는 변하지 않는다.")
    void getPayload_WhenModifyOriginal_UnChangedOriginal() {
        // given
        Map<String, Object> payload = new HashMap<>();
        payload.put(MessageFixture.KEY, MessageFixture.VALUE);

        Message message = Message.create(payload);

        // when
        payload.put("newKey", "newValue");

        Map<String, Object> found = message.getPayload();

        // then
        assertThat(found)
                .isNotEqualTo(payload);
    }

    @Test
    @DisplayName("withEntry()를 호출하면 페이로드가 추가된 새로운 메시지가 반환된다.")
    void withEntry_WhenAddEntry_ReturnNewMessage() {
        // given
        Message message = Message.create();

        // when
        Message found = message.withEntry(MessageFixture.KEY, MessageFixture.VALUE);

        // then
        assertThat(found)
                .isNotEqualTo(message);
    }

    @Test
    @DisplayName("withEntry()를 호출하면 기존 메시지의 페이로드는 변하지 않는다.")
    void withEntry_WhenAddEntry_UnChangedOriginal() {
        // given
        Message message = Message.create();

        // when
        message.withEntry(MessageFixture.KEY, MessageFixture.VALUE);

        String value = message.getPayload(MessageFixture.KEY);

        // then
        assertThat(value)
                .isNull();
    }

    @Test
    @DisplayName("withEntry()를 호출하면 새로운 메시지의 페이로드에 추가된 key-value가 포함된다.")
    void withEntry_WhenAddEntry_NewMessageHasNewEntry() {
        // given
        Message message = Message.create();

        // when
        Message found = message.withEntry(MessageFixture.KEY, MessageFixture.VALUE);

        String value = found.getPayload(MessageFixture.KEY);

        // then
        assertThat(value)
                .isNotNull()
                .isNotEmpty()
                .isEqualTo(MessageFixture.VALUE);
    }

    @Test
    @DisplayName("hasKey()를 호출하면 해당 key의 존재 여부를 반환한다.")
    void hasKey_WhenCheckKey_ReturnsExistence() {
        // given
        Message message = MessageFixture.defaultMessage();

        // when & then
        assertThat(message.hasKey(MessageFixture.KEY))
                .isTrue();

        assertThat(message.hasKey("invalidKey"))
                .isFalse();
    }

    @Test
    @DisplayName("withoutKey()를 호출하면 페이로드가 제거된 새로운 메시지가 반환된다.")
    void withoutKey_WhenRemoveEntry_ReturnNewMessage() {
        // given
        Message message = MessageFixture.defaultMessage();

        // when
        Message found = message.withoutKey(MessageFixture.KEY);

        // then
        assertThat(found)
                .isNotEqualTo(message);
    }

    @Test
    @DisplayName("withoutKey()를 호출하면 기존 메시지의 페이로드는 변하지 않는다.")
    void withoutKey_WhenRemoveEntry_NoChangeOriginal() {
        // given
        Message message = MessageFixture.defaultMessage();

        // when
        message.withoutKey(MessageFixture.KEY);

        // then
        assertThat(message.hasKey(MessageFixture.KEY))
                .isTrue();
    }

    @Test
    @DisplayName("toString()을 호출하면 메시지의 ID와 페이로드가 문자열로 표현된다.")
    void toString_WhenCalled_ReturnsIdAndPayloadString() {
        // given
        Message message = MessageFixture.defaultMessage();

        // when
        String found = message.toString();

        assertThat(found)
                .isNotNull()
                .isNotEmpty()
                .contains(String.valueOf(message.getId()))
                .contains(MessageFixture.KEY)
                .contains(MessageFixture.VALUE);
    }
}