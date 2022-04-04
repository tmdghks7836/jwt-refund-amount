package com.jwt.szs.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class SzsTransactionResult {

    private Boolean isSuccess;

    private String message;
}
