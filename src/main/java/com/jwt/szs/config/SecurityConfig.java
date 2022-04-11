package com.jwt.szs.config;

import com.jwt.szs.filter.*;
import com.jwt.szs.handler.CustomAuthenticationFailureHandler;
import com.jwt.szs.handler.CustomAuthenticationSuccessHandler;
import com.jwt.szs.handler.JwtAuthenticationEntryPoint;
import com.jwt.szs.security.provider.CustomAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    private final JwtTokenFilter jwtTokenFilter;

    private final JwtExceptionFilter jwtExceptionFilter;

    private final RequestUrlLoggingFilter requestUrlLoggingFilter;

    private final CustomAuthenticationFailureHandler authenticationFailureHandler;

    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    private final CustomAuthenticationProvider authenticationProvider;

    // 커스텀 인증 필터
    @Bean
    public CustomAuthenticationFilter customAuthenticationProcessingFilter() throws Exception {
        CustomAuthenticationFilter filter = new CustomAuthenticationFilter("/szs/login");
        filter.setAuthenticationManager(authenticationManager());
        filter.setAuthenticationFailureHandler(authenticationFailureHandler);
        filter.setAuthenticationSuccessHandler(customAuthenticationSuccessHandler);
        return filter;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder authenticationManagerBuilder) {
        authenticationManagerBuilder
                .authenticationProvider(authenticationProvider);
    }

    //TODO swagger, h2-console 은 local profile 설정으로 지정해야함
    @Override
    public void configure(WebSecurity web) {
        web.ignoring()
                .antMatchers(
                        "/h2-console/**", "/favicon.ico", "/error"
                        , "/csrf", "/v3/api-docs", "/configuration/**",
                        "/swagger*/**", "/webjars/**"
                );
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {


        httpSecurity
                .csrf().disable()
                .cors()
                .and()
                .formLogin().disable()
                // 세션을 사용하지 않기 때문에 STATELESS로 설정
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers(apiPathToAllow()).permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(customAuthenticationProcessingFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtTokenFilter, CustomAuthenticationFilter.class)
                .addFilterBefore(jwtExceptionFilter, JwtTokenFilter.class)
                .addFilterBefore(requestUrlLoggingFilter, JwtExceptionFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint);
    }

    private String[] apiPathToAllow() {

        String apiV1Path = "/szs/";
        return new String[]{
                apiV1Path + "login",
                apiV1Path + "signup",
                apiV1Path + "token/re-issuance",
        };
    }
}