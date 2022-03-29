package com.jwt.szs.service.strategy;

/**
 * 근로소득 세액 공제 한도 계산
 * */
public interface IncomeTaxLimitStrategy {

    Long calculate(Long paymentAmount);
}
