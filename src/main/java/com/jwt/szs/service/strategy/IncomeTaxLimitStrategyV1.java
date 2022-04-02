package com.jwt.szs.service.strategy;

import com.jwt.szs.service.strategy.base.IncomeTaxLimitStrategy;

public class IncomeTaxLimitStrategyV1 implements IncomeTaxLimitStrategy {

    private final Long minPayment3300 = 33000000l;

    private final Long maxPayment7000 = 70000000l;

    private final Long million74 = 740000l;

    private final Long million66 = 660000l;

    private final Long million50 = 500000l;

    @Override
    public Long calculate(final Long paymentAmount) {

        if (paymentAmount <= minPayment3300) {
            return million74;
        }

        if (paymentAmount > minPayment3300 && paymentAmount <= maxPayment7000) {

            return gt3300Loe7000(paymentAmount);
        }

        return gt7000(paymentAmount);
    }

    private Long gt3300Loe7000(final Long paymentAmount) {

        Long resultPrice = million74 - (long) ((paymentAmount - minPayment3300) * 0.008);

        if (resultPrice < million66) {

            return million66;
        }

        return resultPrice;
    }

    private Long gt7000(final Long paymentAmount) {

        Long resultPrice = million66 - (long) ((paymentAmount - maxPayment7000) * 0.5);

        if (resultPrice < million50) {

            return million50;
        }

        return resultPrice;
    }
}
