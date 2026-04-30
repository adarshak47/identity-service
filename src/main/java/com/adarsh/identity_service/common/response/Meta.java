package com.adarsh.identity_service.common.response;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Meta {

    private final LocalDateTime timestamp;

    private Meta(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public static Meta now() {
        return new Meta(LocalDateTime.now());
    }
}
