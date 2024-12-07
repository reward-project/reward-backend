package com.outsider.reward.domain.store.query.dao;

import com.outsider.reward.domain.store.query.dto.StoreMissionQueryDto;
import com.outsider.reward.domain.store.query.StoreMissionQuery;
import java.util.List;

public interface StoreMissionQueryDaoCustom {
    List<StoreMissionQueryDto> findByCondition(StoreMissionQuery query);
} 