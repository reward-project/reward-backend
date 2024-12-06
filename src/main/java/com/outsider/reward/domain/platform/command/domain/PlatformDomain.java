package com.outsider.reward.domain.platform.command.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(builderMethodName = "internalBuilder")
public class PlatformDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "platform_id", nullable = false)
    private Platform platform;

    @Column(nullable = false)
    private String domain;

    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PlatformDomainStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public PlatformDomain(Platform platform, String domain, String description) {
        this.platform = platform;
        this.domain = domain;
        this.description = description;
        this.status = PlatformDomainStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public static PlatformDomain.PlatformDomainBuilder builder(Platform platform, String domain, PlatformDomainStatus status) {
        return internalBuilder()
                .platform(platform)
                .domain(domain)
                .status(status)
                .createdAt(LocalDateTime.now());
    }

    public static PlatformDomain createDomain(Platform platform, String domain, PlatformDomainStatus status) {
        return builder(platform, domain, status).build();
    }

    public void approve() {
        this.status = PlatformDomainStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public void reject() {
        this.status = PlatformDomainStatus.REJECTED;
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        this.status = PlatformDomainStatus.INACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean getIsActive() {
        return this.status == PlatformDomainStatus.ACTIVE;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void setStatus(PlatformDomainStatus status) {
        this.status = status;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }
}
