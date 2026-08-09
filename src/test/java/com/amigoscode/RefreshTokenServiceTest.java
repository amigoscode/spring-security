package com.amigoscode;

import com.amigoscode.auth.refresh.RefreshToken;
import com.amigoscode.auth.refresh.RefreshTokenRepository;
import com.amigoscode.auth.refresh.RefreshTokenResponse;
import com.amigoscode.auth.refresh.RefreshTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private ApplicationUserRepository applicationUserRepository;
    @InjectMocks
    private RefreshTokenService underTest;

    private ApplicationUser applicationUser;
    private RefreshToken currentToken;

    @BeforeEach
    void setUp() {
        applicationUser = new ApplicationUser("iakovos", "encryptedPassword", Set.of());
        currentToken =  new RefreshToken(
                "current-token",
                applicationUser,
                LocalDateTime.now().plusDays(3),
                LocalDateTime.now().minusDays(4)
        );
    }

    @Test
    @DisplayName("Generate token User not found - throws")
    void generateRefreshToken_shouldThrowWhenUserNotFound(){
        //user not found  =
        //1.applicationUserRepository returns Optional.empty()
        //2.method throws new UsernameNotFoundException("User not found")
        //3.refreshTokenRepository.save will NOT execute.
        given(applicationUserRepository.findApplicationUserByUsername(anyString()))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.generateRefreshToken("iakovos"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found");

        then(applicationUserRepository).should(times(1)).findApplicationUserByUsername(anyString());
        then(refreshTokenRepository).should(never()).save(any());
        then(applicationUserRepository).shouldHaveNoMoreInteractions();
        then(refreshTokenRepository).shouldHaveNoInteractions();

    }

    @Test
    @DisplayName("Generate token - success")
    void generateRefreshToken_shouldGenerateRefreshToken(){
        //generate token successfully =
        //1.applicationUserRepository.findApplicationUserByUsername returns an existing ApplicationUser
        //2.refreshTokenRepository.save() saves successfully a new RefreshToken
        //3.returns a RefreshTokenResponse
        //4.applicationUserRepository.findApplicationUserByUsername was called only once
        //5.refreshTokenRepository.save() was called only once


        given(applicationUserRepository.findApplicationUserByUsername(anyString()))
                .willReturn(Optional.of(applicationUser));

        /*
         * I used willAnswer() here so save() returns the exact RefreshToken created and passed, without creating a separate instance
         * willAnswer() dynamically determines what to return based on the invocation. You want to return the same object that was passed to save()
         * willReturn() always returns a predefined object. You have a known, predefined currentToken
         *
         * If I used given(refreshTokenRepository.save(currentToken)).willReturn(currentToken);
         * I get "org.mockito.exceptions.misusing.PotentialStubbingProblem: Strict stubbing argument mismatch."
         * */
        given(refreshTokenRepository.save(any(RefreshToken.class)))
                .willAnswer(invocation -> invocation.getArgument(0));


        RefreshTokenResponse response = underTest.generateRefreshToken("iakovos");

        assertThat(response.applicationUser()).isEqualTo(applicationUser);
        then(applicationUserRepository).should(times(1)).findApplicationUserByUsername(anyString());
        then(refreshTokenRepository).should(times(1)).save(any(RefreshToken.class));
        then(applicationUserRepository).shouldHaveNoMoreInteractions();
        then(refreshTokenRepository).shouldHaveNoMoreInteractions();

    }

    @Test
    @DisplayName("Rotate Invalid token - throws")
    void rotateAndGetNewToken_shouldThrowWhenTokenIsInvalid(){
        //token is invalid =
        //1.refreshTokenRepository.findByToken(token) returns Optional.empty()
        //2.method throws new BadCredentialsException("Invalid refresh token")
        //3.refreshTokenRepository.save() will NOT execute.

        given(refreshTokenRepository.findByToken(anyString())).willReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.rotateAndGetNewToken("invalid-token"))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Invalid refresh token");

        then(refreshTokenRepository).should(times(1)).findByToken(anyString());
        then(refreshTokenRepository).should(never()).save(any(RefreshToken.class));

    }

    @Test
    @DisplayName("Rotate Token already Revoked - throws")
    void rotateAndGetNewToken_shouldThrowWhenTokenIsAlreadyRevoked(){
        //token already revoked =
        //1.refreshTokenRepository.findByToken returns a RefreshToken -> use the currentToken
        //2.currentToken.isRevoked() is true
        //3.method throws  BadCredentialsException("Refresh token is revoked")
        //4.refreshTokenRepository.save will NOT execute

        currentToken.setRevoked(true);
        given(refreshTokenRepository.findByToken("current-token"))
                .willReturn(Optional.of(currentToken));


        assertThatThrownBy(() -> underTest.rotateAndGetNewToken("current-token"))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Refresh token is revoked");

        then(refreshTokenRepository).should(times(1)).findByToken(anyString());
        then(refreshTokenRepository).should(never()).save(any(RefreshToken.class));
        then(refreshTokenRepository).shouldHaveNoMoreInteractions();

    }

    @Test
    @DisplayName("Rotate Token is expired - throws")
    void rotateAndGetNewToken_shouldThrowWhenTokenIsExpired(){
        //token is expired =
        //1.refreshTokenRepository.findByToken() returns a RefreshToken -> use the currentToken and change the expireDate
        //2.currentToken getExpiresAt().isBefore(LocalDateTime.now())
        //3.refreshTokenRepository.save will NOT execute

        currentToken.setExpiresAt(LocalDateTime.now().minusDays(1));
        given(refreshTokenRepository.findByToken("current-token")).willReturn(Optional.of(currentToken));

        assertThatThrownBy(() -> underTest.rotateAndGetNewToken("current-token"))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Refresh token is expired");

        then(refreshTokenRepository).should(times(1)).findByToken(anyString());
        then(refreshTokenRepository).should(never()).save(any(RefreshToken.class));
        then(refreshTokenRepository).shouldHaveNoMoreInteractions();

    }

    @Test
    @DisplayName("Rotate and Generate Refresh Token - success")
    void rotateAndGetNewToken_shouldRotateAndGetNewToken(){
        // Revoke and Generate Refresh Token =
        //1.refreshTokenRepository.findByToken() returns a RefreshToken - use currentToken
        //2.ensure currentToken = true
        //3.refreshTokenRepository.save(currentToken) will save the currentToken which revoked
        //4.refreshTokenRepository.save() will be called for new RefreshToken
        //5.method will return new RefreshTokenResponse with the new token.

        /*
         * I used willAnswer() here so save() returns the exact RefreshToken created and passed, without creating a separate instance
         * willAnswer() dynamically determines what to return based on the invocation. You want to return the same object that was passed to save()
         * willReturn() always returns a predefined object. You have a known, predefined currentToken
         * */
        given(refreshTokenRepository.findByToken("current-token")).willReturn(Optional.of(currentToken));
        given(refreshTokenRepository.save(any(RefreshToken.class))).willAnswer(invocation -> invocation.getArgument(0));

        RefreshTokenResponse response = underTest.rotateAndGetNewToken("current-token");


        ArgumentCaptor<RefreshToken> refreshTokenCaptor = ArgumentCaptor.forClass(RefreshToken.class);
        assertThat(response.refreshToken()).isNotEqualTo(currentToken.getToken());
        assertThat(response.applicationUser()).isEqualTo(applicationUser);

        then(refreshTokenRepository).should(times(1)).findByToken(anyString());
        then(refreshTokenRepository).should(times(2)).save(refreshTokenCaptor.capture());
        then(refreshTokenRepository).shouldHaveNoMoreInteractions();
        RefreshToken revokedToken = refreshTokenCaptor.getAllValues().getFirst();
        assertThat(revokedToken.getToken()).isEqualTo("current-token");
        assertThat(revokedToken.isRevoked()).isTrue();

    }

    @Test
    @DisplayName("Revoke Invalid token - throws")
    void revokeRefreshToken_shouldThrowWhenTokenIsInvalid(){
        given(refreshTokenRepository.findByToken("invalid-token")).willReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.revokeRefreshToken("invalid-token"))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Invalid refresh token");

        then(refreshTokenRepository).should().findByToken("invalid-token");
        then(refreshTokenRepository).should(never()).save(any(RefreshToken.class));

    }


    @Test
    @DisplayName("Revoke Token already Revoked - throws")
    void revokeRefreshToken_shouldThrowWhenTokenIsAlreadyRevoked(){
        currentToken.setRevoked(true);
        given(refreshTokenRepository.findByToken(anyString())).willReturn(Optional.of(currentToken));

        assertThatThrownBy(() -> underTest.revokeRefreshToken("already-revoked-token"))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Refresh token is revoked");


        then(refreshTokenRepository).should().findByToken(anyString());
        then(refreshTokenRepository).should(never()).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("Revoke Token is expired - throws")
    void revokeRefreshToken_shouldThrowWhenTokenIsExpired(){
        currentToken.setExpiresAt(LocalDateTime.now().minusDays(1));
        given(refreshTokenRepository.findByToken("expired-token")).willReturn(Optional.of(currentToken));

        assertThatThrownBy(() -> underTest.revokeRefreshToken("expired-token"))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Refresh token is expired");

        then(refreshTokenRepository).should(times(1)).findByToken(anyString());
        then(refreshTokenRepository).should(never()).save(any(RefreshToken.class));
    }


    @Test
    @DisplayName("Revoke Refresh Token - success")
    void revokeRefreshToken_shouldRevokeRefreshToken(){
        given(refreshTokenRepository.findByToken("current-token")).willReturn(Optional.of(currentToken));

        underTest.revokeRefreshToken("current-token");

        ArgumentCaptor<RefreshToken> refreshTokenCaptor = ArgumentCaptor.forClass(RefreshToken.class);

        then(refreshTokenRepository).should().findByToken("current-token");
        then(refreshTokenRepository).should().save(refreshTokenCaptor.capture());

        RefreshToken revokedToken = refreshTokenCaptor.getValue();
        assertThat(revokedToken.isRevoked()).isTrue();
        assertThat(revokedToken.getToken()).isEqualTo("current-token");

    }
}
