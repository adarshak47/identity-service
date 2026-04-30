package com.adarsh.identity_service.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final boolean success;
    private final T data;
    private final ErrorResponse error;
    private final Meta meta;

    private ApiResponse(boolean success, T data, ErrorResponse error, Meta meta) {
        this.success = success;
        this.data = data;
        this.error = error;
        this.meta = meta;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null, Meta.now());
    }

    public static <T> ApiResponse<T> failure(ErrorResponse error) {
        return new ApiResponse<>(false, null, error, Meta.now());
    }
}
