package com.amigoscode;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class JpaUserDetailsService implements UserDetailsService {

    private final ApplicationUserRepository applicationUserRepository;

    public JpaUserDetailsService(ApplicationUserRepository applicationUserRepository) {
        this.applicationUserRepository = applicationUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return applicationUserRepository.findApplicationUserByUsername(username)
                .map(au -> {
                    Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
                    au.getAppUserRoles().forEach(role -> {
                        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
                        role.getPermissions().forEach(permission -> {
                            grantedAuthorities.add(new SimpleGrantedAuthority(permission.getName()));
                        });
                    });
                    return new User(
                            au.getUsername(),
                            au.getPassword(),
                            au.isEnabled(),
                            !au.isAccountExpired(),
                            !au.isCredentialsExpired(),
                            !au.isAccountLocked(),
                            grantedAuthorities);
                        }
                )
                .orElseThrow(() ->
                        new UsernameNotFoundException("app user not found with username: " + username));
    }
}
