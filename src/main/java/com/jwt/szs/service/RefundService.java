package com.jwt.szs.service;

import com.jwt.szs.service.strategy.IncomeTaxLimitStrategy;
import com.jwt.szs.service.strategy.IncomeTaxStrategy;
import com.jwt.szs.service.strategy.RefundStrategy;
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

    public Long calculateAmount(Long paymentAmount, Long calculatedTax){

        Long incomeTaxAmount = incomeTaxStrategy.calculate(calculatedTax);
        Long incomeTaxLimitAmount = incomeTaxLimitStrategy.calculate(paymentAmount);
        log.info("근로소득 세액공제 한도 : {}", incomeTaxLimitAmount);
        log.info("근로소득 세액공제 금액 : {}", incomeTaxAmount);

        return refundStrategy.calculate(incomeTaxLimitAmount, incomeTaxAmount);
    }
}
