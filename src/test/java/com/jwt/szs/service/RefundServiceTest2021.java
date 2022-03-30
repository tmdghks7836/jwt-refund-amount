package com.jwt.szs.service;

import com.jwt.szs.service.strategy.IncomeTaxLimitStrategy2021;
import com.jwt.szs.service.strategy.IncomeTaxStrategy2021;
import com.jwt.szs.service.strategy.RefundStrategy2021;
import com.jwt.szs.service.strategy.base.IncomeTaxLimitStrategy;
import com.jwt.szs.service.strategy.base.IncomeTaxStrategy;
import com.jwt.szs.service.strategy.base.RefundStrategy;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * 2021년도 환급액 계산
 */
@SpringBootTest(properties = "spring.main.allow-bean-definition-overriding=true", webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RefundServiceTest2021 {

    @Autowired
    RefundService refundService;

    @Autowired
    IncomeTaxStrategy incomeTaxStrategy;

    @Autowired
    IncomeTaxLimitStrategy incomeTaxLimitStrategy;

    @Autowired
    RefundStrategy refundStrategy;

    @Slf4j
    @TestConfiguration
    public static class RefundStrategyTestConfig2021 {

        @Bean
        public IncomeTaxLimitStrategy incomeTaxLimitStrategy() {

            log.info("2021년도 세액 공제 한도계산 전략을 bean 으로 등록.");
            return new IncomeTaxLimitStrategy2021();
        }

        @Bean
        public IncomeTaxStrategy incomeTaxStrategy() {

            log.info("2021년도 세액 공제 계산 전략을 bean 으로 등록.");
            return new IncomeTaxStrategy2021();
        }

        @Bean
        public RefundStrategy refundStrategy() {

            log.info("2021년도 환급 계산 전략을 bean 으로 등록.");
            return new RefundStrategy2021();
        }
    }

    @Test
    void 환급액_계산() {

        Long calculateAmount = refundService.calculateAmount(40000000l, 2000000l);

        Assertions.assertEquals(calculateAmount, 684000l);
    }

    @Test
    void 근로소득_세액공제_한도계산_3300만원이하() {

        Long calculateAmount = incomeTaxLimitStrategy.calculate(33000000l);

        Assertions.assertEquals(calculateAmount, 740000);
    }

    @Test
    void 근로소득_세액공제_한도계산_3300만원미만() {

        Long calculateAmount2 = incomeTaxLimitStrategy.calculate(32999999l);

        Assertions.assertEquals(calculateAmount2, 740000);
    }

    @Test
    void 근로소득_세액공제_한도계산_33000초과_7000이하() {

        Long calculateAmount = incomeTaxLimitStrategy.calculate(40000000l);

        Assertions.assertEquals(calculateAmount, 684000l);
    }

    @Test
    void 근로소득_세액공제_한도계산_33000초과_7000이하2() {

        Long calculateAmount = incomeTaxLimitStrategy.calculate(68000000l);

        Assertions.assertEquals(calculateAmount, 660000);
    }

    @Test
    void 근로소득_세액공제_한도계산_7000만원초과() {

        Long calculateAmount = incomeTaxLimitStrategy.calculate(100000000l);

        Assertions.assertEquals(calculateAmount, 500000);
    }

    @Test
    void 근로소득_세액공제계산130만원이하() {

        Long calculatedTax = 1300000l;

        Long calculateAmount = incomeTaxStrategy.calculate(calculatedTax);

        Assertions.assertEquals(calculateAmount, (long) (calculatedTax * 0.55));
    }

    @Test
    void 근로소득_세액공제계산130만원미만() {

        Long calculatedTax = 1299999l;

        Long calculateAmount = incomeTaxStrategy.calculate(calculatedTax);

        Assertions.assertEquals(calculateAmount, (long) (calculatedTax * 0.55));
    }

    @Test
    void 근로소득_세액공제계산130만원초과() {

        Long calculateAmount = incomeTaxStrategy.calculate(2000000l);

        Assertions.assertEquals(calculateAmount, 925000);
    }
}