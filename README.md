# Spring Security

![Spring Security Architecture](Spring%20Security%20Architecture.png)

A hands-on course that walks through Spring Security from first principles up to JWT-secured REST APIs. Each section builds directly on the previous one, ending with a working Spring Boot application that authenticates users, authorizes requests, and issues JWTs.

> Course: [amigoscode.com/courses/spring-security](https://amigoscode.com/courses/spring-security)

## What you'll build

A Spring Boot app (Java 25, Spring Boot 4) that:

- Authenticates users via form login, HTTP Basic, and JWT
- Loads users from a database with a custom `UserDetailsService`
- Stores hashed passwords with BCrypt
- Models roles and permissions with JPA and `GrantedAuthority`
- Authorizes requests using `@PreAuthorize` and request matchers
- Reacts to authentication events (success, failure, lockout)
- Issues and validates JWTs as a Spring resource server

## Prerequisites

- Java 25
- Maven (the Maven wrapper is included — use `./mvnw`)
- Basic Spring Boot familiarity (controllers, beans, JPA)

## Run the app

```bash
./mvnw spring-boot:run
```

## Branches

Each section has a matching branch. Check out the `*-start` branch to follow along, or the section branch to see the finished state.

| Section branch              | Starter branch                  |
| --------------------------- | ------------------------------- |
| `form-login`                | `starter`                       |
| `basic-authentication`      | `basic-authentication-starter`  |
| `authentication`            | `authentication-starter`        |
| `user-details-service`      | `user-details-service-start`    |
| `security-context-holder`   | `security-context-holder-start` |
| `roles-and-permission`      | `roles-and-permissions-start`   |
| `security-events`           | `security-events-starter`       |
| `jwt`                       | `jtw-starter`                   |

## Course outline

### 1. Intro
- What is Spring Security

### 2. Form Login
- Form login intro, implementation, and testing
- `JSESSIONID` and storing it in Redis / JDBC
- Form login configuration
- Inspecting the filter chain

### 3. Basic Auth
- Configuring HTTP Basic
- Basic Auth in the browser vs server-to-server
- Configuration and CSRF

### 4. Authentication
- `AuthenticationProvider` and `UserDetailsService`
- Noop passwords, hashing, and BCrypt
- Custom `DaoAuthenticationProvider`
- Using the `AuthenticationManager`

### 5. User Details Service
- Custom `UserDetailsService`
- The `ApplicationUser` entity and its annotations
- Inspecting tables, persisting users, and fixing `loadByUsername`

### 6. Security Context Holder
- Reading from `SecurityContextHolder`
- Injecting `Authentication` as a controller parameter

### 7. Roles and Permissions
- Role and permission entities and ERD
- Saving roles and permissions through repositories
- Mapping authorities with `SimpleGrantedAuthority`

### 8. Authorization
- `@PreAuthorize` and `hasAuthority`
- Request matchers and other authorization methods

### 9. Security Events
- Capturing authentication events
- Async event handling
- Exercise

### 10. JWT
- Intro to JWT and required dependencies
- Enabling the resource server
- Encoding/decoding with a secret key
- `JwtTokenService` and `JwtDecoder`
- Inspecting tokens, the auth controller, and JWT in action
- Embedding roles and permissions in the token
- `JwtAuthenticationConverter`

## How to use this repo

1. Check out `starter` to begin.
2. Watch a lesson, then implement it on the matching `*-start` branch.
3. Compare your work against the section branch when you're done.
4. Move to the next section.
