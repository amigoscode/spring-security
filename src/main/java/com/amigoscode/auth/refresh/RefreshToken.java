package com.amigoscode.auth.refresh;

import com.amigoscode.ApplicationUser;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(
        name = "refresh_token",
        indexes = {
                @Index(
                        name = "idx_refresh_token_user",
                        columnList = "application_user_id"
                )
        }
)
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "application_user_id", nullable = false)
    private ApplicationUser applicationUser;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private Boolean isRevoked = false;

    public RefreshToken(String token,
                        ApplicationUser applicationUser,
                        LocalDateTime expiresAt,
                        LocalDateTime createdAt) {
        this.token = token;
        this.applicationUser = applicationUser;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
    }

    public RefreshToken() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public ApplicationUser getApplicationUser() {
        return applicationUser;
    }

    public void setApplicationUser(ApplicationUser applicationUser) {
        this.applicationUser = applicationUser;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean isRevoked() {
        return isRevoked;
    }

    public void setRevoked(Boolean revoked) {
        isRevoked = revoked;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RefreshToken that = (RefreshToken) o;
        return Objects.equals(id, that.id) && Objects.equals(token, that.token) && Objects.equals(applicationUser, that.applicationUser) && Objects.equals(expiresAt, that.expiresAt) && Objects.equals(createdAt, that.createdAt) && Objects.equals(isRevoked, that.isRevoked);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, token, applicationUser, expiresAt, createdAt, isRevoked);
    }
}
