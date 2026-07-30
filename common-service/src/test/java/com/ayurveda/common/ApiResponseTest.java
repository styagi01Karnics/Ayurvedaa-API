package com.ayurveda.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ApiResponseTest {

    @Test
    void successWithData_setsDefaults() {
        ApiResponse<String> response = ApiResponse.success("payload");

        assertTrue(response.isSuccess());
        assertEquals(200, response.getStatus());
        assertEquals("Success", response.getMessage());
        assertEquals("payload", response.getData());
    }

    @Test
    void successWithMessage_usesCustomMessage() {
        ApiResponse<Integer> response = ApiResponse.success("Created", 1);

        assertTrue(response.isSuccess());
        assertEquals("Created", response.getMessage());
        assertEquals(1, response.getData());
    }

    @Test
    void failure_setsErrorState() {
        ApiResponse<Void> response = ApiResponse.failure(400, "Bad request");

        assertFalse(response.isSuccess());
        assertEquals(400, response.getStatus());
        assertEquals("Bad request", response.getMessage());
        assertNull(response.getData());
    }

    @Test
    void failureWithData_keepsPayload() {
        ApiResponse<String> response = ApiResponse.failure(404, "Missing", "detail");

        assertFalse(response.isSuccess());
        assertEquals("detail", response.getData());
    }

}
