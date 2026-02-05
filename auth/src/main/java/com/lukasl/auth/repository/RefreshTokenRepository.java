package com.lukasl.auth.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.lukasl.auth.entity.RefreshToken;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    
    Optional<RefreshToken> findByToken(String token);
    
    @Modifying
    @Query("""
        DELETE FROM RefreshToken rt 
        WHERE rt.user.id = :userId
    """)
    void deleteByUserId(UUID userId);
    
    @Modifying
    @Query("""
        UPDATE RefreshToken rt 
        SET rt.revoked = true 
        WHERE rt.user.id = :userId
    """)
    void revokeAllByUserId(UUID userId);
}
