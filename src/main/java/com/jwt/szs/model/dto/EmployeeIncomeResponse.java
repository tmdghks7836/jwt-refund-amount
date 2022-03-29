package com.jwt.szs.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
@AllArgsConstructor
public class EmployeeIncomeResponse {

    private String name;

    private String taxLimitAmount;

    private String taxAmount;

    private String refundAmount;
}
