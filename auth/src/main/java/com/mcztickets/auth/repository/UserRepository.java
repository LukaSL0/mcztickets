package com.mcztickets.auth.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mcztickets.auth.entity.User;
import com.mcztickets.auth.enums.UserRole;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    List<User> findByRole(UserRole role);

    @Cacheable(value = "users", key = "#username")
    Optional<User> findByUsername(String username);

    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

}
