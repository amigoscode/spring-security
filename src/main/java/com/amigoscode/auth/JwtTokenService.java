package com.amigoscode.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.SECONDS;

@Service
public class JwtTokenService {
    private final JwtEncoder jwtEncoder;

    public JwtTokenService(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    String generateToken(
            String subject,
            Collection<? extends GrantedAuthority> authorities
    ) {
        Instant now = Instant.now();
        var roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .filter(r -> r.startsWith("ROLE_"))
                .map(r -> r.substring("ROLE_".length()))
                .toList();
        var permissions = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> !authority.startsWith("ROLE_") && !authority.startsWith("FACTOR_"))
                .toList();

        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .issuer("https://amigoscode.com")
                .issuedAt(now)
                .expiresAt(now.plus(30, SECONDS))
                .subject(subject)
                .claim("roles", roles)
                .claim("permissions", permissions)
                .build();
        JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS256).build();
        JwtEncoderParameters jwtEncoderParameters = JwtEncoderParameters.from(
                jwsHeader,
                claimsSet
        );
        return jwtEncoder.encode(jwtEncoderParameters).getTokenValue();
    }
}
