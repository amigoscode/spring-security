package com.amigoscode;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Service
public class AuthEventListener {

    @EventListener
    @Async
    public void onAuthenticationSuccess(AuthenticationSuccessEvent e) throws InterruptedException {
        System.out.println("AuthenticationSuccessEvent");
        System.out.println(Thread.currentThread().getName());
        Thread.sleep(Duration.of(10, ChronoUnit.SECONDS));
        System.out.println(e.getAuthentication().getName());
        System.out.println();
    }

    @EventListener
    public void onSuccess(AbstractAuthenticationFailureEvent e) {
        System.out.println("AbstractAuthenticationFailureEvent");
        System.out.println(e.getAuthentication().getName());
        System.out.println(e.getException().getMessage());
        System.out.println();
    }

}
