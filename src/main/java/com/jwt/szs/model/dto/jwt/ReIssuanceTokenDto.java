package com.jwt.szs.model.dto.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
@AllArgsConstructor
public class ReIssuanceTokenDto {

    private String accessToken;

    private String refreshToken;
}
