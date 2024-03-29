package com.jwt.szs.utils;

import com.jwt.szs.model.base.BaseMember;
import com.jwt.szs.model.dto.member.UserDetailsImpl;
import com.jwt.szs.model.type.JwtTokenType;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
public class JwtTokenUtils {

    private static final String JWT_SECRET_KEY = "zdtlD3JK56m6wTTgsNFhqzjqPdsafevvvdsaeasfdxcz";

    private static final String jwtIssuer = "seungHwan";

    public static String getUserId(String token) {
        return extractAllClaims(token).get("userId", String.class);
    }

    public static String getName(String token) {
        return extractAllClaims(token).get("name", String.class);
    }

    public static Long getId(String token) {
        return extractAllClaims(token).get("id", Long.class);
    }

    public static JwtTokenType getTokenType(String token) {

        String type = extractAllClaims(token).get("type", String.class);

        return JwtTokenType.findByCookieName(type);
    }

    public static String[] getRoles(String token) {

        String rolePayload = extractAllClaims(token).get("role", String.class);

        return StringUtils.hasText(rolePayload) ? rolePayload.split(",") : null;
    }

    public static Boolean isTokenExpired(String token) {

        try {
            parseClaims(token);
        } catch (ExpiredJwtException e) {
            return true;
        }
        return false;
    }

    private static Key getSigningKey() {
        byte[] keyBytes = JWT_SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public static String generateToken(BaseMember baseMember, JwtTokenType tokenType) {

        return generateToken(baseMember, tokenType, tokenType.getValidationSeconds());
    }

    public static String generateToken(BaseMember baseMember, JwtTokenType tokenType, long expireTime) {

        Claims claims = Jwts.claims();
        claims.put("id", baseMember.getId());
        claims.put("userId", baseMember.getUserId());
        claims.put("name", baseMember.getName());
        claims.put("type", tokenType.getCookieName());

        log.info(tokenType.getCookieName());
        return generateToken(
                claims,
                expireTime
        );
    }

    public static String generateToken(Claims claims, long expireTime) {

        log.info("expireDate =>{}", new Date(System.currentTimeMillis() + expireTime));

        String jwt = Jwts.builder()
                .setClaims(claims)
                .setIssuer(jwtIssuer)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expireTime))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();

        return jwt;
    }

    public static boolean validate(String token) {

        try {
            if (!StringUtils.hasText(token)) {
                return false;
            }

            parseClaims(token);

        } catch (JwtException e) {

            log.error("jwt 인증에 실패하였습니다. {}", e.getMessage());
            throw e;
        }

        return true;
    }

    /**
     * 사용하기 전 반드시 토큰 검증해야 합니다.
     */
    public static Claims extractAllClaims(String token) {

        return parseClaims(token).getBody();
    }

    private static Jws<Claims> parseClaims(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
    }

    public Authentication getAuthentication(String token) {

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(getRoles(token))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        UserDetailsImpl userDetails = new UserDetailsImpl(getId(token), getUserId(token));

        return new UsernamePasswordAuthenticationToken(userDetails, token, authorities);
    }

    public static Cookie createRefreshTokenCookie(String value) {

        JwtTokenType tokenType = getTokenType(value);

        if (!tokenType.equals(JwtTokenType.REFRESH)) {
            throw new BadCredentialsException("did not match token Type");
        }
        return CookieUtil.createCookie(
                tokenType.getCookieName(),
                value,
                tokenType.getValidationSeconds());
    }

    public static Cookie createAccessTokenCookie(String value) {

        JwtTokenType tokenType = getTokenType(value);

        if (!tokenType.equals(JwtTokenType.ACCESS)) {
            throw new BadCredentialsException("did not match token Type");
        }

        return CookieUtil.createCookie(
                tokenType.getCookieName(),
                value,
                tokenType.getValidationSeconds());
    }

    public static Cookie createCookie(JwtTokenType tokenType, String value) {
        return CookieUtil.createCookie(tokenType.getCookieName(), value);
    }

    public static Cookie findCookie(HttpServletRequest request, JwtTokenType tokenType) {
        return CookieUtil.getCookie(request, tokenType.getCookieName());
    }

    public static String generateAccessToken(BaseMember baseMember) {
        return generateToken(baseMember, JwtTokenType.ACCESS);
    }

    public static String generateRefreshToken(BaseMember baseMember) {
        return generateToken(baseMember, JwtTokenType.REFRESH);
    }
}
