package com.outsider.reward.domain.store.query.dto;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class StoreInfo {
    private final String storeName;
    private final String productLink;
    private final String keyword;
    private final String productId;
    private final String storeStatus;
}