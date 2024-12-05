package com.outsider.reward.domain.store.command.domain.service;

import com.outsider.reward.domain.store.command.domain.StoreMission;
import com.outsider.reward.domain.store.exception.StoreMissionException;
import com.outsider.reward.domain.store.exception.StoreMissionErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.outsider.reward.domain.store.command.domain.StoreMissionRepository;
import com.outsider.reward.domain.store.command.dto.CreateStoreMissionRequest;
import com.outsider.reward.domain.store.command.mapper.StoreMissionMapper;

import java.time.LocalDate;

@Service
public class StoreMissionDomainService {

    private final StoreMissionRepository storeMissionRepository;

    public StoreMissionDomainService(StoreMissionRepository storeMissionRepository) {
        this.storeMissionRepository = storeMissionRepository;
    }

    public void validateStoreMission(StoreMission storeMission) {
        validateDates(storeMission);
        validateProductLink(storeMission);
    }

    @Transactional
    public StoreMission createStoreMission(CreateStoreMissionRequest request) {
        StoreMission storeMission = StoreMissionMapper.INSTANCE.toEntity(request);
        return storeMissionRepository.save(storeMission);
    }

    private void validateDates(StoreMission storeMission) {
        if (storeMission.getEndDate().isBefore(storeMission.getStartDate())) {
            throw new StoreMissionException(StoreMissionErrorCode.INVALID_DATE_RANGE);
        }
        
        LocalDate today = LocalDate.now();
        if (storeMission.getStartDate().isBefore(today)) {
            throw new StoreMissionException(StoreMissionErrorCode.PAST_START_DATE);
        }
    }

    private void validateProductLink(StoreMission storeMission) {
        String productLink = storeMission.getProductLink();
        try {
            if (!java.net.URI.create(productLink).isAbsolute()) {
                throw new StoreMissionException(StoreMissionErrorCode.INVALID_PRODUCT_LINK);
            }
        } catch (IllegalArgumentException e) {
            throw new StoreMissionException(StoreMissionErrorCode.INVALID_PRODUCT_LINK, e);
        }
    }
}
