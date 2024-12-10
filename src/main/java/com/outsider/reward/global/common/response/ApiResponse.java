package com.outsider.reward.global.common.response;

import org.springframework.data.domain.Page;
import lombok.Getter;

@Getter
public class ApiResponse<T> {
    private final T data;
    private final String message;
    private final boolean success;

    private ApiResponse(T data, String message, boolean success) {
        this.data = data;
        this.message = message;
        this.success = success;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(data, null, true);
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(data, message, true);
    }

    public static <T> ApiResponse<T> error(T data, String message) {
        return new ApiResponse<>(data, message, false);
    }

    public static <T> ApiResponse<T> ok(T data) {
        return success(data);
    }
}