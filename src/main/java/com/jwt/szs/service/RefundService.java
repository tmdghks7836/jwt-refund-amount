package com.jwt.szs.service;

import com.jwt.szs.service.strategy.base.IncomeTaxLimitStrategy;
import com.jwt.szs.service.strategy.base.IncomeTaxStrategy;
import com.jwt.szs.service.strategy.base.RefundStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefundService {

    private final IncomeTaxStrategy incomeTaxStrategy;

    private final IncomeTaxLimitStrategy incomeTaxLimitStrategy;

    private final RefundStrategy refundStrategy;

    public Long getIncomeTax(final Long calculatedTax) {
        return incomeTaxStrategy.calculate(calculatedTax);
    }

    public Long getIncomeTaxLimit(final Long paymentAmount) {
        return incomeTaxLimitStrategy.calculate(paymentAmount);
    }

    public Long calculateAmount(final Long paymentAmount,final Long calculatedTax) {

        Long incomeTaxAmount = getIncomeTax(calculatedTax);
        Long incomeTaxLimitAmount = getIncomeTaxLimit(paymentAmount);
        log.info("근로소득 세액공제 한도 : {}", incomeTaxLimitAmount);
        log.info("근로소득 세액공제 금액 : {}", incomeTaxAmount);

        return refundStrategy.calculate(incomeTaxLimitAmount, incomeTaxAmount);
    }
}
