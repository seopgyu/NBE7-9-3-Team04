package com.backend.api.user.dto.response;

public record TokenResponse(String accessToken, String refreshToken) {
}
