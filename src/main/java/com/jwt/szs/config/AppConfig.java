package com.jwt.szs.config;

import com.jwt.szs.filter.JwtTokenFilter;
import com.jwt.szs.filter.strategy.CheckJwtBearerTokenStrategy;
import com.jwt.szs.filter.strategy.CheckJwtTokenStrategy;
import com.jwt.szs.service.strategy.*;
import com.jwt.szs.service.strategy.base.IncomeTaxLimitStrategy;
import com.jwt.szs.service.strategy.base.IncomeTaxStrategy;
import com.jwt.szs.service.strategy.base.RefundStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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
        return new IncomeTaxStrategy2021();
    }

    @Bean
    public IncomeTaxLimitStrategy incomeTaxLimitStrategy(){
        return new IncomeTaxLimitStrategy2021();
    }

    @Bean
    public RefundStrategy refundStrategy(){
        return new RefundStrategy2021();
    }
}
