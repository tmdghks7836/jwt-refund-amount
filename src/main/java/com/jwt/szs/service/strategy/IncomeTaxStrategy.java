package com.jwt.szs.service.strategy;

/**
 * 근로소득 세액공제 계산
 * */
public interface IncomeTaxStrategy {

    Long calculate(Long calculatedTax);
}
