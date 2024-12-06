package com.outsider.reward.domain.platform.command.application;

import com.outsider.reward.domain.platform.command.domain.Platform;
import com.outsider.reward.domain.platform.command.domain.PlatformRepository;
import com.outsider.reward.domain.platform.command.domain.PlatformStatus;
import com.outsider.reward.domain.platform.command.domain.PlatformDomain;
import com.outsider.reward.domain.platform.command.domain.PlatformDomainRepository;
import com.outsider.reward.domain.platform.command.domain.PlatformDomainStatus;
import com.outsider.reward.domain.platform.command.dto.AddPlatformDomainRequest;
import com.outsider.reward.domain.platform.command.dto.CreatePlatformRequest;
import com.outsider.reward.domain.platform.command.dto.PlatformResponse;
import com.outsider.reward.domain.platform.command.dto.PlatformDomainResponse;
import com.outsider.reward.domain.platform.command.mapper.PlatformMapper;
import com.outsider.reward.domain.platform.exception.PlatformException;
import com.outsider.reward.domain.platform.exception.PlatformErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlatformService {
    private final PlatformRepository platformRepository;
    private final PlatformMapper platformMapper;
    private final PlatformDomainRepository platformDomainRepository;


    @Transactional
    public PlatformResponse createPlatform(CreatePlatformRequest request) {
        if (platformRepository.existsByName(request.getName())) {
            throw new PlatformException(PlatformErrorCode.DUPLICATE_PLATFORM);
        }

        // 모든 도메인에 대해 중복 검사
        for (String domain : request.getDomains()) {
            if (platformDomainRepository.existsByDomainAndStatus(domain, PlatformDomainStatus.PENDING)) {
                throw new PlatformException(PlatformErrorCode.DUPLICATE_PLATFORM_DOMAIN);
            }
        }

        Platform platform = platformMapper.toEntity(request);
        
        // 모든 도메인 추가
        for (String domain : request.getDomains()) {
            platform.addDomain(domain);
        }
        
        platform = platformRepository.save(platform);
        return platformMapper.toResponse(platform);
    }

    @Transactional
    public PlatformResponse approvePlatform(Long platformId) {
        Platform platform = getPlatform(platformId);
        platform.approve();
        return platformMapper.toResponse(platform);
    }

    @Transactional
    public PlatformResponse rejectPlatform(Long platformId) {
        Platform platform = getPlatform(platformId);
        platform.reject();
        return platformMapper.toResponse(platform);
    }

    @Transactional
    public void addDomain(Long platformId, AddPlatformDomainRequest request) {
        Platform platform = getPlatform(platformId);
        
        if (platformDomainRepository.existsByDomainAndStatus(request.getDomain(), PlatformDomainStatus.PENDING)) {
            throw new PlatformException(PlatformErrorCode.DUPLICATE_PLATFORM_DOMAIN);
        }

        platform.addDomain(request.getDomain());
    }

    @Transactional(readOnly = true)
    public List<PlatformResponse> getActivePlatforms() {
        return platformRepository.findByStatus(PlatformStatus.ACTIVE)
                .stream()
                .map(platformMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PlatformResponse> getPendingPlatforms() {
        return platformRepository.findByStatus(PlatformStatus.PENDING)
                .stream()
                .map(platformMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PlatformResponse> searchPlatforms(String searchTerm) {
        return platformRepository.findByNameContainingIgnoreCaseOrDisplayNameContainingIgnoreCase(
                searchTerm, searchTerm)
                .stream()
                .map(platformMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public boolean isDomainAvailable(String domain) {
        return !platformDomainRepository.existsByDomainAndStatus(domain, PlatformDomainStatus.ACTIVE);
    }

    @Transactional(readOnly = true)
    public List<PlatformDomainResponse> getPlatformDomains(Long platformId) {
        Platform platform = platformRepository.findById(platformId)
                .orElseThrow(() -> new IllegalArgumentException("Platform not found"));
        return platform.getDomains().stream()
                .map(PlatformDomainResponse::from)
                .collect(Collectors.toList());
    }

    private Platform getPlatform(Long platformId) {
        return platformRepository.findById(platformId)
                .orElseThrow(() -> new PlatformException(PlatformErrorCode.PLATFORM_NOT_FOUND));
    }
}
