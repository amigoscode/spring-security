package com.amigoscode;

import com.amigoscode.auth.JwtTokenService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;

@EnableAsync
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

	@GetMapping("api/v2/me")
	public Map<String, Object> me2(@AuthenticationPrincipal Jwt jwt) {

		return Map.of(
				"subject", jwt.getSubject(),
				"issuer", jwt.getIssuer(),
				"issuedAt", jwt.getIssuedAt(),
				"expiresAt", jwt.getExpiresAt(),
				"roles", jwt.getClaimAsStringList("roles"),
				"permissions", jwt.getClaimAsStringList("permissions")
		);
	}

	@Bean
	CommandLineRunner commandLineRunner(
			ApplicationUserRepository applicationUserRepository,
			RoleRepository roleRepository,
			PermissionRepository permissionRepository,
			PasswordEncoder passwordEncoder,
			JwtTokenService jwtTokenService) {
		return args -> {
			Permission userRead = permissionRepository.save(new Permission("user:read"));
			Permission userDelete = permissionRepository.save(new Permission("user:delete"));
			Permission userUpdate = permissionRepository.save(new Permission("user:update"));

			Role adminRole = roleRepository.save(
					new Role(
						"ADMIN",
						Set.of(userRead, userDelete, userUpdate)
					)
			);

			Role managerRole = roleRepository.save(
					new Role(
							"MANAGER",
							Set.of(userRead, userUpdate)
					)
			);


			Role userRole = roleRepository.save(
					new Role(
							"USER"
					)
			);

			ApplicationUser amigoscode = new ApplicationUser(
					"amigoscode",
					passwordEncoder.encode("password"),
					Set.of(adminRole)
			);

			ApplicationUser thays = new ApplicationUser(
					"thays",
					passwordEncoder.encode("password"),
					Set.of(managerRole)
			);

			ApplicationUser ollayor = new ApplicationUser(
					"ollayor",
					passwordEncoder.encode("password"),
					Set.of(userRole)
			);


			applicationUserRepository.saveAll(List.of(amigoscode, thays, ollayor));
		};
	}

}
