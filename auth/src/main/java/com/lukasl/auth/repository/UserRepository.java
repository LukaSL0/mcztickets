package com.lukasl.auth.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lukasl.auth.entity.User;
import com.lukasl.auth.enums.UserRole;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    List<User> findByRole(UserRole role);

    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);

    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

}
