package com.amigoscode;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JpaUserDetailsService implements UserDetailsService {

    private final ApplicationUserRepository applicationUserRepository;

    public JpaUserDetailsService(ApplicationUserRepository applicationUserRepository) {
        this.applicationUserRepository = applicationUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return applicationUserRepository.findApplicationUserByUsername(username)
                .map(au -> new User(
                        au.getUsername(),
                        au.getPassword(),
                        au.isEnabled(),
                        !au.isAccountExpired(),
                        !au.isCredentialsExpired(),
                        !au.isAccountLocked(),
                        List.of() // TODO: fix this
                ))
                .orElseThrow(() ->
                        new UsernameNotFoundException("app user not found with username: " + username));
    }
}
