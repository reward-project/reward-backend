package com.outsider.reward.domain.finance.command.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KakaoPayReadyResponse {
    private String tid;
    private String nextRedirectPcUrl;
    private String nextRedirectMobileUrl;
    private String nextRedirectAppUrl;
    private String androidAppScheme;
    private String iosAppScheme;
    private String createdAt;
} 