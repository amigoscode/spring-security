package com.amigoscode;

import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Service;

@Service
public class LoginAttemptService {

    public void onFailure(AuthenticationFailureBadCredentialsEvent e) {

    }

    public void onSuccess(AuthenticationSuccessEvent e) {

    }

}
