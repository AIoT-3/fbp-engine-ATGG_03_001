package com.nhnacademy.fbp.infrastructure.http.annotation;

import com.nhnacademy.fbp.infrastructure.http.exception.MethodNotSupportedException;

public enum RequestMethod {
    GET,
    POST,
    DELETE;

    public static RequestMethod resolve(String method) {
        try {
            return RequestMethod.valueOf(method);
        } catch (IllegalArgumentException e) {
            throw new MethodNotSupportedException("지원하지 않는 메서드입니다.");
        }
    }
}
