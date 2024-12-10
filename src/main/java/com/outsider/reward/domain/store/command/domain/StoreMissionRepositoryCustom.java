package com.outsider.reward.domain.store.command.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import com.outsider.reward.domain.store.query.dto.StoreMissionQueryResponse;

public interface StoreMissionRepositoryCustom {
    Page<StoreMissionQueryResponse> findAllActiveMissionsWithCompletionStatus(Long userId, LocalDate date, Pageable pageable);
}
