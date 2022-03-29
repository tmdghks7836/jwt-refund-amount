package com.jwt.szs.service.strategy;

import com.jwt.szs.model.entity.EmployeeIncome;

/**
 * 환급액 계산
 * */
public interface RefundStrategy {

    Long calculate(Long incomeTaxLimitAmount, Long incomeTaxAmount);
}
