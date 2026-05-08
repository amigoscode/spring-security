package com.amigoscode.auth.refresh;

import com.amigoscode.ApplicationUser;

import java.time.LocalDateTime;

public record RefreshTokenResponse(
        String refreshToken,
        LocalDateTime expiresAt,
        ApplicationUser applicationUser
) {
}
