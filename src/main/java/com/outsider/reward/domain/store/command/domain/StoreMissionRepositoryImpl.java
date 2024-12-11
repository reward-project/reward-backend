package com.outsider.reward.domain.store.command.domain;

import com.outsider.reward.domain.finance.command.domain.QRewardBudget;
import com.outsider.reward.domain.member.command.domain.QMember;
import com.outsider.reward.domain.platform.command.domain.QPlatform;
import com.outsider.reward.domain.store.command.domain.QStoreMission;
import com.outsider.reward.domain.store.query.dto.StoreMissionQueryResponse;
import com.outsider.reward.domain.store.query.mapper.StoreMissionQueryMapper;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class StoreMissionRepositoryImpl implements StoreMissionRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    private final StoreMissionQueryMapper storeMissionMapper;

    @Override
    public Page<StoreMissionQueryResponse> findAllActiveMissionsWithCompletionStatus(
            Long userId, LocalDate date, Pageable pageable) {
        
        QStoreMission mission = QStoreMission.storeMission;
        QMissionCompletion completion = QMissionCompletion.missionCompletion;
        QRewardUsage usage = QRewardUsage.rewardUsage;
        QRewardBudget budget = QRewardBudget.rewardBudget;
        
        // 메인 쿼리
        JPQLQuery<Tuple> query = queryFactory
            .select(
                mission,
                completion.isNotNull()
            )
            .from(mission)
            .leftJoin(completion)
            .on(completion.mission.id.eq(mission.id)
                .and(completion.userId.eq(userId)))
            .leftJoin(mission.budget, budget)
            .leftJoin(usage)
            .on(usage.mission.id.eq(mission.id)
                .and(usage.user.id.eq(userId)))
            .where(
                isWithinDateRange(mission, date),
                hasRemainingBudget(budget),
                hasRemainingDailyRewards(mission, usage, date),
                usage.isNull().or(usage.status.ne(RewardUsageStatus.COMPLETED))  // 성공한 미션은 제외
            );

        // 페이징 적용
        JPQLQuery<Tuple> pageQuery = query
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize());

        // 결과 조회 및 DTO 변환
        List<StoreMissionQueryResponse> content = pageQuery.fetch().stream()
            .map(tuple -> {
                StoreMission storeMission = tuple.get(0, StoreMission.class);
                boolean completed = tuple.get(1, Boolean.class);
                return storeMissionMapper.toResponse(storeMission, completed);
            })
            .collect(Collectors.toList());

        // 전체 카운트 쿼리 (최적화를 위해 카운트 쿼리는 조인을 최소화)
        JPQLQuery<Long> countQuery = queryFactory
            .select(mission.count())
            .from(mission)
            .leftJoin(mission.budget, budget)
            .leftJoin(usage)
            .on(usage.mission.id.eq(mission.id)
                .and(usage.user.id.eq(userId)))
            .where(
                isWithinDateRange(mission, date),
                hasRemainingBudget(budget),
                hasRemainingDailyRewards(mission, usage, date),
                usage.isNull().or(usage.status.ne(RewardUsageStatus.COMPLETED))  // 성공한 미션은 제외
            );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanExpression isWithinDateRange(QStoreMission mission, LocalDate date) {
        return mission.startDate.loe(date)
            .and(mission.endDate.goe(date));
    }

    private BooleanExpression hasRemainingBudget(QRewardBudget budget) {
        return budget.usedBudget.lt(budget.totalBudget)
            .and(budget.usedRewardsToday.lt(budget.maxRewardsPerDay));
    }

    private BooleanExpression hasRemainingDailyRewards(QStoreMission mission, QRewardUsage usage, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

        return JPAExpressions
            .select(usage.count())
            .from(usage)
            .where(usage.mission.id.eq(mission.id)
                .and(usage.usedAt.between(startOfDay, endOfDay)))
            .lt(mission.maxRewardsPerDay.longValue());
    }
}
