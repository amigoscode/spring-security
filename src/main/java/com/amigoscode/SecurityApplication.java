package com.amigoscode;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;

@SpringBootApplication
@RestController
@RequestMapping("/")
public class SecurityApplication {

	static void main(String[] args) {
		SpringApplication.run(SecurityApplication.class, args);
	}

	@GetMapping
	public String foo() {
		return "bar";
	}

	@GetMapping("api/v1/me")
	public Map<String, Object> me(Authentication authentication) {
//		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		return Map.of(
				"authentication.isAuthenticated()", String.valueOf(authentication.isAuthenticated()),
				"authentication.getAuthorities()", String.valueOf(authentication.getAuthorities()),
				"authentication.getCredentials()", String.valueOf(authentication.getCredentials()),
				"authentication.getPrincipal()", String.valueOf(authentication.getPrincipal()),
				"authentication.getDetails()", String.valueOf(authentication.getDetails())
		);
	}

	@Bean
	CommandLineRunner commandLineRunner(
			ApplicationUserRepository repository,
			PasswordEncoder passwordEncoder) {
		return args -> {
			ApplicationUser user = new ApplicationUser(
					"amigoscode", passwordEncoder.encode("password"), Set.of("USER"));
			repository.save(user);
		};
	}

}
