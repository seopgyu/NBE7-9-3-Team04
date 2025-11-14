package com.backend.api.user.dto.response

@JvmRecord
data class TokenResponse(
    @JvmField val accessToken: String,
    @JvmField val refreshToken: String
)

