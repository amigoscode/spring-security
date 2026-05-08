package com.amigoscode.auth.refresh;

import com.amigoscode.ApplicationUser;
import com.amigoscode.ApplicationUserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final ApplicationUserRepository applicationUserRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository,
                               ApplicationUserRepository applicationUserRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.applicationUserRepository = applicationUserRepository;
    }

    public RefreshTokenResponse generateRefreshToken(String username) {
        ApplicationUser applicationUser = applicationUserRepository
                .findApplicationUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return generateRefreshToken(applicationUser);
    }

    private RefreshTokenResponse generateRefreshToken(ApplicationUser applicationUser) {
        if (applicationUser == null) {
            throw new IllegalArgumentException("ApplicationUser cannot be null");
        }

        RefreshToken refreshToken = refreshTokenRepository.save(
                new RefreshToken(
                        UUID.randomUUID().toString(),
                        applicationUser,
                        LocalDateTime.now().plusDays(7),
                        LocalDateTime.now()
                )
        );
        return new RefreshTokenResponse(
                refreshToken.getToken(),
                refreshToken.getExpiresAt(),
                applicationUser
        );
    }

    @Transactional
    public RefreshTokenResponse rotateAndGetNewToken(String token) {
        RefreshToken currentRefreshToken = refreshTokenRepository
                .findByToken(token)
                .orElseThrow(() -> new BadCredentialsException("Invalid refresh token"));

        if(currentRefreshToken.isRevoked()) {
            throw new BadCredentialsException("Refresh token is revoked");
        }

        if(currentRefreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadCredentialsException("Refresh token is expired");
        }

        currentRefreshToken.setRevoked(true);
        refreshTokenRepository.save(currentRefreshToken);
        ApplicationUser applicationUser = currentRefreshToken.getApplicationUser();
        return generateRefreshToken(applicationUser);
    }
}
