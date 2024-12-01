package com.outsider.reward.domain.member.command.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@NoArgsConstructor
@RedisHash(value = "refreshToken", timeToLive = 1209600) // 14일
public class RefreshToken {
    
    @Id  // org.springframework.data.annotation.Id 사용
    private String refreshToken;  // refreshToken 자체를 ID로 사용
    private String email;         // email은 값으로 저장
    
    public RefreshToken(String refreshToken, String email) {
        this.refreshToken = refreshToken;
        this.email = email;
    }
    
    public String getRefreshToken() {
        return refreshToken;
    }
    
    public String getEmail() {
        return email;
    }
} 