package com.amigoscode.auth;

public record LoginResponse(
        String accessToken,
        String refreshToken
) {
}
