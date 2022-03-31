package com.jwt.szs.filter;

import com.jwt.szs.filter.strategy.CheckJwtTokenStrategy;
import com.jwt.szs.model.dto.jwt.IssueTokenResponse;
import com.jwt.szs.model.dto.jwt.ReIssuanceTokenDto;
import com.jwt.szs.model.dto.member.AuthenticationMemberPrinciple;
import com.jwt.szs.model.type.JwtTokenType;
import com.jwt.szs.service.RefreshTokenService;
import com.jwt.szs.utils.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
@Component
public class ReIssuanceAccessTokenFilter extends OncePerRequestFilter {

    private final CheckJwtTokenStrategy checkJwtTokenStrategy;

    private final RefreshTokenService refreshTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        if(!request.getRequestURI().equals("/szs/token/re-issuance")){
            chain.doFilter(request, response);
            return;
        }

        String refreshToken = JwtTokenUtils.findCookie(request, JwtTokenType.REFRESH).getValue();
        String accessToken = checkJwtTokenStrategy.getTokenByRequest(request);

        ReIssuanceTokenDto reIssuanceTokenDto = ReIssuanceTokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        String reIssuanceAccessToken = refreshTokenService.ReIssuanceAccessToken(reIssuanceTokenDto);

        checkJwtTokenStrategy.setResponseToken(response, reIssuanceAccessToken);
    }
}
