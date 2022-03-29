package com.jwt.szs.service.strategy;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RefundStrategy2021 implements RefundStrategy{

    @Override
    public Long calculate(Long incomeTaxLimitAmount, Long incomeTaxAmount) {

        log.info("환급액 = min({},{})", incomeTaxLimitAmount, incomeTaxAmount);
        return  Math.min(incomeTaxLimitAmount, incomeTaxAmount);
    }
}
