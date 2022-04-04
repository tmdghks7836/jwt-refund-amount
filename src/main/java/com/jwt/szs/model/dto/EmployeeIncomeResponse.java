package com.jwt.szs.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
@AllArgsConstructor
public class EmployeeIncomeResponse {

    @JsonProperty(value = "이름")
    private String name;

    @JsonProperty(value = "한도")
    private String taxLimitAmount;

    @JsonProperty(value = "공제액")
    private String taxAmount;

    @JsonProperty(value = "환급액")
    private String refundAmount;
}
