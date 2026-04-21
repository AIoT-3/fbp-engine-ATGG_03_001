package com.nhnacademy.fbp.infrastructure.http.exception;

public class MethodNotSupportedException extends RuntimeException {
    public MethodNotSupportedException(String message) {
        super(message);
    }
}
