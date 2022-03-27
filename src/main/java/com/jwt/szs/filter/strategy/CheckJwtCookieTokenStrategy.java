package com.jwt.szs.filter.strategy;

import com.jwt.szs.model.type.JwtTokenType;
import com.jwt.szs.utils.CookieUtil;
import com.jwt.szs.utils.JwtTokenUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 쿠키 값으로 jwt token을 검증합니다.
 * */
public class CheckJwtCookieTokenStrategy implements CheckJwtTokenStrategy {

    @Override
    public void setResponseToken(HttpServletResponse response, String token) {

        response.addCookie(JwtTokenUtils.createAccessTokenCookie(token));
    }

    @Override
    public String getTokenByRequest(HttpServletRequest request) {

        Cookie[] cookies = request.getCookies();

        if (cookies == null || cookies.length == 0) {
            return null;
        }

        Cookie jwtCookie = CookieUtil.getCookie(
                request,
                JwtTokenType.ACCESS.getCookieName()
        );

        if (jwtCookie == null) {
           return null;
        }

        // Get jwt token and validate
        return jwtCookie.getValue();
    }
}
