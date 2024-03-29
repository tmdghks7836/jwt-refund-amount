package com.jwt.szs.service.strategy;

import com.jwt.szs.service.strategy.base.IncomeTaxStrategy;

/**
 * 근로소득 세액공제 계산 2021
 */
public class IncomeTaxStrategyV1 implements IncomeTaxStrategy {

    public Long calculate(final Long calculatedTax) {

        final Long baseAmount = 1300000l;

        if (calculatedTax <= baseAmount) {
            return (long) (calculatedTax * 0.55);
        }

        return 715000 + (long)((calculatedTax - baseAmount) * 0.3);
    }
}
