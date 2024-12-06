package com.outsider.reward.domain.platform.command.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.outsider.reward.domain.platform.command.domain.PlatformDomain;
import com.outsider.reward.domain.platform.command.domain.PlatformDomainStatus;
import com.outsider.reward.domain.platform.command.domain.PlatformStatus;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Platform {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String displayName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlatformStatus status;

    private String description;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "platform", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlatformDomain> domains = new ArrayList<>();

    @Builder
    public Platform(String name, String displayName, String description) {
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.status = PlatformStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public void approve() {
        this.status = PlatformStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public void reject() {
        this.status = PlatformStatus.REJECTED;
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        this.status = PlatformStatus.INACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public void addDomain(String domain) {
        if (domain == null) {
            throw new IllegalArgumentException("Domain cannot be null");
        }
        PlatformDomain platformDomain = PlatformDomain.createDomain(this, domain, PlatformDomainStatus.ACTIVE);
        this.domains.add(platformDomain);
    }

    public boolean hasDomain(String domain) {
        return domains.stream()
                .anyMatch(platformDomain -> platformDomain.getDomain().equals(domain));
    }

    public void setDomains(List<PlatformDomain> domains) {
        this.domains = domains;
        if (domains != null) {
            domains.forEach(domain -> domain.setPlatform(this));
        }
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
