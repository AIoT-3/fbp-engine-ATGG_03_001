package com.nhnacademy.fbp.infrastructure.http.dto;

public record ResponseEntity<T>(
        int status,
        T body
) {
    public static <T> ResponseEntity<T> of(int status, T body) {
        return new ResponseEntity<>(status, body);
    }

    public static <T> ResponseEntity<T> ok(T body) {
        return new ResponseEntity<>(200, body);
    }

    public static ResponseEntity<Void> created() {
        return new ResponseEntity<>(201, null);
    }

    public static ResponseEntity<Void> noContent() {
        return new ResponseEntity<>(204, null);
    }
}
