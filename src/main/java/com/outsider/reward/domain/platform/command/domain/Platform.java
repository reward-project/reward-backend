package com.outsider.reward.domain.platform.command.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
}
