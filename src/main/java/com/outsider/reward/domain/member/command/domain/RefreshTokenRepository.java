package com.outsider.reward.domain.member.command.domain;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
    Optional<RefreshToken> findByRefreshToken(String refreshToken);
    List<RefreshToken> findAllByEmail(String email);
    void deleteByRefreshToken(String refreshToken);
} 