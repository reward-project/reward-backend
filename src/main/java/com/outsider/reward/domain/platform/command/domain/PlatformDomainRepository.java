package com.outsider.reward.domain.platform.command.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlatformDomainRepository extends JpaRepository<PlatformDomain, Long> {
    List<PlatformDomain> findByPlatformAndStatus(Platform platform, PlatformDomainStatus status);
    boolean existsByDomainAndStatus(String domain, PlatformDomainStatus status);
}
