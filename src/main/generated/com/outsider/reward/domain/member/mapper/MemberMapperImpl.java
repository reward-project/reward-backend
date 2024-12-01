package com.outsider.reward.domain.member.mapper;

import com.outsider.reward.domain.member.command.domain.Member;
import com.outsider.reward.domain.member.command.domain.MemberBasicInfo;
import com.outsider.reward.domain.member.query.dto.MemberQuery;
import com.outsider.reward.domain.member.query.dto.MemberResponse;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-12-01T19:34:39+0900",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.11.1.jar, environment: Java 21.0.3 (Eclipse Adoptium)"
)
@Component
public class MemberMapperImpl implements MemberMapper {

    @Override
    public MemberResponse toResponse(Member member) {
        if ( member == null ) {
            return null;
        }

        MemberResponse.MemberResponseBuilder memberResponse = MemberResponse.builder();

        memberResponse.email( memberBasicInfoEmail( member ) );
        memberResponse.name( memberBasicInfoName( member ) );
        memberResponse.nickname( memberBasicInfoNickname( member ) );
        memberResponse.profileImage( member.getProfileImageUrl() );
        memberResponse.id( member.getId() );
        memberResponse.createdAt( member.getCreatedAt() );

        return memberResponse.build();
    }

    @Override
    public MemberQuery.MemberInfo toMemberInfo(Member member) {
        if ( member == null ) {
            return null;
        }

        String email = null;
        String name = null;
        String nickname = null;
        String profileImageUrl = null;
        Long id = null;

        email = memberBasicInfoEmail( member );
        name = memberBasicInfoName( member );
        nickname = memberBasicInfoNickname( member );
        profileImageUrl = member.getProfileImageUrl();
        id = member.getId();

        MemberQuery.MemberInfo memberInfo = new MemberQuery.MemberInfo( id, name, email, nickname, profileImageUrl );

        return memberInfo;
    }

    private String memberBasicInfoEmail(Member member) {
        if ( member == null ) {
            return null;
        }
        MemberBasicInfo basicInfo = member.getBasicInfo();
        if ( basicInfo == null ) {
            return null;
        }
        String email = basicInfo.getEmail();
        if ( email == null ) {
            return null;
        }
        return email;
    }

    private String memberBasicInfoName(Member member) {
        if ( member == null ) {
            return null;
        }
        MemberBasicInfo basicInfo = member.getBasicInfo();
        if ( basicInfo == null ) {
            return null;
        }
        String name = basicInfo.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }

    private String memberBasicInfoNickname(Member member) {
        if ( member == null ) {
            return null;
        }
        MemberBasicInfo basicInfo = member.getBasicInfo();
        if ( basicInfo == null ) {
            return null;
        }
        String nickname = basicInfo.getNickname();
        if ( nickname == null ) {
            return null;
        }
        return nickname;
    }
}
