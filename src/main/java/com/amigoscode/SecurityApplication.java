package com.amigoscode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
