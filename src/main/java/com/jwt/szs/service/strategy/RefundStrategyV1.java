package com.jwt.szs.service.strategy;

import com.jwt.szs.service.strategy.base.RefundStrategy;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RefundStrategyV1 implements RefundStrategy {

    @Override
    public Long calculate(final Long incomeTaxLimitAmount, final Long incomeTaxAmount) {

        log.info("환급액 = min({},{})", incomeTaxLimitAmount, incomeTaxAmount);
        return Math.min(incomeTaxLimitAmount, incomeTaxAmount);
    }
}
