package com.jwt.szs.config;

import com.jwt.szs.filter.JwtTokenFilter;
import com.jwt.szs.filter.strategy.CheckJwtBearerTokenStrategy;
import com.jwt.szs.filter.strategy.CheckJwtTokenStrategy;
import com.jwt.szs.service.strategy.*;
import com.jwt.szs.service.strategy.base.IncomeTaxLimitStrategy;
import com.jwt.szs.service.strategy.base.IncomeTaxStrategy;
import com.jwt.szs.service.strategy.base.RefundStrategy;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

@Configuration
@RequiredArgsConstructor
public class AppConfig {


    @Bean
    public JwtTokenFilter jwtTokenCookieFilter(){
        return new JwtTokenFilter(checkJwtTokenStrategy());
    }

    @Bean
    public CheckJwtTokenStrategy checkJwtTokenStrategy(){
        return new CheckJwtBearerTokenStrategy();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public IncomeTaxStrategy incomeTaxStrategy(){
        return new IncomeTaxStrategyV1();
    }

    @Bean
    public IncomeTaxLimitStrategy incomeTaxLimitStrategy(){
        return new IncomeTaxLimitStrategyV1();
    }

    @Bean
    public RefundStrategy refundStrategy(){
        return new RefundStrategyV1();
    }

    @Bean
    JPAQueryFactory jpaQueryFactory(EntityManager em) {
        return new JPAQueryFactory(em);
    }

    @PostConstruct
    void postConstruct() {

        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));

    }
}
