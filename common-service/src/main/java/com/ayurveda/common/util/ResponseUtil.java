package com.ayurveda.common.util;

import com.ayurveda.common.ApiResponse;

//import com.ayurveda.common;

public final class ResponseUtil {

    private ResponseUtil() {
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.success(message, data);
    }

    public static ApiResponse<Void> success(String message) {
        return ApiResponse.success(message, null);
    }

}