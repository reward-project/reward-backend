package com.outsider.reward.domain.member.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.outsider.reward.domain.member.command.domain.Member;
import com.outsider.reward.domain.member.query.dto.MemberResponse;
import com.outsider.reward.domain.member.query.dto.MemberQuery;

@Mapper(componentModel = "spring")
public interface MemberMapper {


    @Mapping(source = "basicInfo.email", target = "email")
    @Mapping(source = "basicInfo.name", target = "name")
    @Mapping(source = "basicInfo.nickname", target = "nickname")
    @Mapping(source = "profileImageUrl", target = "profileImage")
    MemberResponse toResponse(Member member);

    @Mapping(source = "basicInfo.email", target = "email")
    @Mapping(source = "basicInfo.name", target = "name")
    @Mapping(source = "basicInfo.nickname", target = "nickname")
    @Mapping(source = "profileImageUrl", target = "profileImageUrl")
    MemberQuery.MemberInfo toMemberInfo(Member member);
} 