package com.amigoscode;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "application_user")
public class ApplicationUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, name = "role")
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "app_user_role",
            joinColumns = @JoinColumn(name = "application_user_id")
    )
    private Set<String> appUserRoles = new HashSet<>();

    @Column(nullable = false)
    private boolean isEnabled = true;

    @Column(nullable = false)
    private boolean isAccountLocked = false;

    @Column(nullable = false)
    private boolean isAccountExpired = false;

    @Column(nullable = false)
    private boolean isCredentialsExpired = false;
    
    public ApplicationUser() {
    }

    public ApplicationUser(String username, String password, Set<String> appUserRoles) {
        this.username = username;
        this.password = password;
        this.appUserRoles = appUserRoles;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<String> getAppUserRoles() {
        return appUserRoles;
    }

    public void setAppUserRoles(Set<String> appUserRoles) {
        this.appUserRoles = appUserRoles;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public boolean isAccountLocked() {
        return isAccountLocked;
    }

    public void setAccountLocked(boolean accountLocked) {
        isAccountLocked = accountLocked;
    }

    public boolean isAccountExpired() {
        return isAccountExpired;
    }

    public void setAccountExpired(boolean accountExpired) {
        isAccountExpired = accountExpired;
    }

    public boolean isCredentialsExpired() {
        return isCredentialsExpired;
    }

    public void setCredentialsExpired(boolean credentialsExpired) {
        isCredentialsExpired = credentialsExpired;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ApplicationUser that = (ApplicationUser) o;
        return isEnabled == that.isEnabled && isAccountLocked == that.isAccountLocked && isAccountExpired == that.isAccountExpired && isCredentialsExpired == that.isCredentialsExpired && Objects.equals(id, that.id) && Objects.equals(username, that.username) && Objects.equals(password, that.password) && Objects.equals(appUserRoles, that.appUserRoles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, password, appUserRoles, isEnabled, isAccountLocked, isAccountExpired, isCredentialsExpired);
    }
}
