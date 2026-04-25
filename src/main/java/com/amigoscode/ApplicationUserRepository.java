package com.amigoscode;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApplicationUserRepository extends JpaRepository<ApplicationUser, Long> {
    @Query("SELECT u FROM ApplicationUser u LEFT JOIN FETCH u.appUserRoles WHERE u.username = :username")
    Optional<ApplicationUser> findApplicationUserByUsername(@Param("username") String username);
}
