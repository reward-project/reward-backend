package com.outsider.reward.domain.store.query.dao;

import com.outsider.reward.domain.store.query.dto.StoreMissionQueryDto;
import com.outsider.reward.domain.store.command.domain.QStoreMission;
import com.outsider.reward.domain.store.query.StoreMissionQuery;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class StoreMissionQueryDaoImpl implements StoreMissionQueryDaoCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<StoreMissionQueryDto> findByCondition(StoreMissionQuery query) {
        BooleanBuilder builder = new BooleanBuilder();
        
        if (StringUtils.hasText(query.getTag())) {
            builder.and(QStoreMission.storeMission.tags.any().name.containsIgnoreCase(query.getTag()));
        }
        
        return queryFactory
            .select(Projections.constructor(StoreMissionQueryDto.class,
                QStoreMission.storeMission.id,
                QStoreMission.storeMission.rewardId,
                QStoreMission.storeMission.rewardName,
                QStoreMission.storeMission.registrant.basicInfo.name.as("registrantName"),
                QStoreMission.storeMission.registrant.id.as("registrantId"),
                QStoreMission.storeMission.platform,
                QStoreMission.storeMission.storeName,
                QStoreMission.storeMission.productLink,
                QStoreMission.storeMission.keyword,
                QStoreMission.storeMission.productId,
                QStoreMission.storeMission.startDate,
                QStoreMission.storeMission.endDate,
                QStoreMission.storeMission.rewardAmount,
                QStoreMission.storeMission.maxRewardsPerDay
            ))
            .from(QStoreMission.storeMission)
            .where(builder)
            .fetch();
    }
} 