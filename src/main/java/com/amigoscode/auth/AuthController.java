package com.amigoscode.auth;

import com.amigoscode.ApplicationUser;
import com.amigoscode.ApplicationUserRepository;
import com.amigoscode.auth.refresh.RefreshTokenRequest;
import com.amigoscode.auth.refresh.RefreshTokenService;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("api/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;
    private final RefreshTokenService refreshTokenService;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtTokenService jwtTokenService,
                          RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("login")
    public LoginResponse login(@Valid @RequestBody LoginRequest loginRequest) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password());
        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        String accessToken = jwtTokenService.generateToken(
                authentication.getName(),
                authentication.getAuthorities()
        );
        var refreshToken = refreshTokenService.generateRefreshToken(loginRequest.username());
        return new LoginResponse(
                accessToken,
                refreshToken.refreshToken()
        );
    }

    @PostMapping("refresh")
    public LoginResponse refreshToken(
            @RequestBody @Valid RefreshTokenRequest currentRefreshToken) {
        var newRefreshTokenResponse = refreshTokenService.
                rotateAndGetNewToken(currentRefreshToken.refreshToken());
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
                newRefreshTokenResponse.refreshToken()
        );
        return loginResponse;
    }

    // TODO: /logout -> should revoke access token
    //   > user sends the refresh token only
    // TODO: /logout-all -> should revoke all access token
    //   > user needs to send JWT > subject > then logout all
}
