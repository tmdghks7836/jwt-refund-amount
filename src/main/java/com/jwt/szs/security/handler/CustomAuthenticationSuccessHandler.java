package com.jwt.szs.security.handler;

import com.jwt.szs.filter.strategy.CheckJwtTokenStrategy;
import com.jwt.szs.model.dto.member.AuthenticationMemberPrinciple;
import com.jwt.szs.model.type.JwtTokenType;
import com.jwt.szs.service.RefreshTokenService;
import com.jwt.szs.utils.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author 이승환
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final CheckJwtTokenStrategy jwtTokenStrategy;

    private final RefreshTokenService refreshTokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response, Authentication authentication) {

        log.info("success login!!");

        AuthenticationMemberPrinciple principal = (AuthenticationMemberPrinciple) authentication.getPrincipal();

        final String refreshJwt = JwtTokenUtils.generateRefreshToken(principal.getUserId());

        refreshTokenService.createRefreshToken(refreshJwt, principal.getUserId(), JwtTokenType.REFRESH.getValidationSeconds());

        response.addCookie(JwtTokenUtils.createRefreshTokenCookie(refreshJwt));

        jwtTokenStrategy.setResponseToken(response, principal.getToken());
    }

}
