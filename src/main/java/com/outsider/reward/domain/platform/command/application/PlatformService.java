package com.outsider.reward.domain.platform.command.application;

import com.outsider.reward.domain.platform.command.domain.*;
import com.outsider.reward.domain.platform.command.dto.AddPlatformDomainRequest;
import com.outsider.reward.domain.platform.command.dto.CreatePlatformRequest;
import com.outsider.reward.domain.platform.command.dto.PlatformResponse;
import com.outsider.reward.domain.platform.exception.PlatformException;
import com.outsider.reward.domain.platform.exception.PlatformErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlatformService {
    private final PlatformRepository platformRepository;
    private final PlatformDomainRepository platformDomainRepository;

    @Transactional
    public PlatformResponse createPlatform(CreatePlatformRequest request) {
        if (platformRepository.existsByName(request.getName())) {
            throw new PlatformException(PlatformErrorCode.DUPLICATE_PLATFORM);
        }

        Platform platform = Platform.builder()
                .name(request.getName())
                .displayName(request.getDisplayName())
                .description(request.getDescription())
                .build();

        return PlatformResponse.from(platformRepository.save(platform));
    }

    @Transactional
    public PlatformResponse approvePlatform(Long platformId) {
        Platform platform = getPlatform(platformId);
        platform.approve();
        return PlatformResponse.from(platform);
    }

    @Transactional
    public PlatformResponse rejectPlatform(Long platformId) {
        Platform platform = getPlatform(platformId);
        platform.reject();
        return PlatformResponse.from(platform);
    }

    @Transactional
    public void addDomain(Long platformId, AddPlatformDomainRequest request) {
        Platform platform = getPlatform(platformId);
        
        if (platformDomainRepository.existsByDomainAndIsActiveTrue(request.getDomain())) {
            throw new PlatformException(PlatformErrorCode.DUPLICATE_PLATFORM_DOMAIN);
        }

        PlatformDomain domain = PlatformDomain.builder()
                .platform(platform)
                .domain(request.getDomain())
                .description(request.getDescription())
                .build();

        platformDomainRepository.save(domain);
    }

    @Transactional(readOnly = true)
    public List<PlatformResponse> getActivePlatforms() {
        return platformRepository.findByStatus(PlatformStatus.ACTIVE)
                .stream()
                .map(PlatformResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PlatformResponse> getPendingPlatforms() {
        return platformRepository.findByStatus(PlatformStatus.PENDING)
                .stream()
                .map(PlatformResponse::from)
                .toList();
    }

    private Platform getPlatform(Long platformId) {
        return platformRepository.findById(platformId)
                .orElseThrow(() -> new PlatformException(PlatformErrorCode.PLATFORM_NOT_FOUND));
    }
}
