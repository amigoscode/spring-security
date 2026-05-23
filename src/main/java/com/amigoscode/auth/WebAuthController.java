package com.amigoscode.auth;

import com.amigoscode.ApplicationUser;
import com.amigoscode.auth.refresh.RefreshTokenRequest;
import com.amigoscode.auth.refresh.RefreshTokenService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("api/v1/web/auth")
public class WebAuthController {

    private static final String REFRESH_TOKEN = "refresh_token";
    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;
    private final RefreshTokenService refreshTokenService;

    public WebAuthController(AuthenticationManager authenticationManager,
                             JwtTokenService jwtTokenService,
                             RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password());
        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        String accessToken = jwtTokenService.generateToken(
                authentication.getName(),
                authentication.getAuthorities()
        );
        var refreshToken = refreshTokenService.generateRefreshToken(loginRequest.username());
        ResponseCookie cookie = ResponseCookie.from(
                        REFRESH_TOKEN,
                        refreshToken.refreshToken()
                )
                .maxAge(Duration.ofDays(7))
                .httpOnly(true)
                .secure(true)
                .path("/api/v1/web/auth")
                .sameSite("Strict")
                .build();
        System.out.println(cookie);
        LoginResponse loginResponse = new LoginResponse(accessToken, null);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(loginResponse);

    }

    @PostMapping("refresh")
    public ResponseEntity<LoginResponse> refreshToken(
            @CookieValue(value = REFRESH_TOKEN) String currentRefreshToken) {
        var newRefreshTokenResponse = refreshTokenService.
                rotateAndGetNewToken(currentRefreshToken);
        ApplicationUser applicationUser = newRefreshTokenResponse.applicationUser();

        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        applicationUser.getAppUserRoles().forEach(role -> {
            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
            role.getPermissions().forEach(permission -> {
                grantedAuthorities.add(new SimpleGrantedAuthority(permission.getName()));
            });
        });

        var accessToken = jwtTokenService.generateToken(
                applicationUser.getUsername(),
                grantedAuthorities
        );

        LoginResponse loginResponse = new LoginResponse(
                accessToken,
                null
        );

        ResponseCookie cookie = ResponseCookie.from(
                        REFRESH_TOKEN,
                        newRefreshTokenResponse.refreshToken()
                )
                .maxAge(Duration.ofDays(7))
                .httpOnly(true)
                .secure(true)
                .path("/api/v1/web/auth")
                .sameSite("Strict")
                .build();
        System.out.println(cookie);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(loginResponse);
    }

    @PostMapping("logout")
    public ResponseEntity<Void> logOut(@CookieValue(value = REFRESH_TOKEN) String currentRefreshToken) {
        refreshTokenService.revokeRefreshToken(currentRefreshToken);
        ResponseCookie cookie = ResponseCookie.from(
                        REFRESH_TOKEN,
                        ""
                )
                .maxAge(Duration.ofDays(0))
                .httpOnly(true)
                .secure(true)
                .path("/api/v1/web/auth")
                .sameSite("Strict")
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }
}
