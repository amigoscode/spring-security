package com.amigoscode.auth.refresh;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
        @NotBlank(message = "Refresh token is required") String refreshToken) {
}
