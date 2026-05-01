package com.amigoscode.management;

import com.amigoscode.ApplicationUser;
import com.amigoscode.ApplicationUserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/management/users")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
public class UserController {

    private final ApplicationUserRepository applicationUserRepository;

    public UserController(ApplicationUserRepository applicationUserRepository) {
        this.applicationUserRepository = applicationUserRepository;
    }

    @GetMapping
    public List<ApplicationUser> getAllUsers() {
        return applicationUserRepository.findAll();
    }

    @PreAuthorize("hasAuthority('user:delete')")
    @DeleteMapping
    public String delete() {
        return "deleted";
    }

}
