package com.outsider.reward.domain.platform.command.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlatformRepository extends JpaRepository<Platform, Long> {
    Optional<Platform> findByName(String name);
    List<Platform> findByStatus(PlatformStatus status);
    boolean existsByName(String name);
}
