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

	@Bean
	CommandLineRunner commandLineRunner(
			ApplicationUserRepository repository,
			PasswordEncoder passwordEncoder) {
		return args -> {
			ApplicationUser user = new ApplicationUser(
					"amigoscode", passwordEncoder.encode("password"), Set.of("USER"));
			user.setAccountLocked(true);
			repository.save(user);
		};
	}

}
