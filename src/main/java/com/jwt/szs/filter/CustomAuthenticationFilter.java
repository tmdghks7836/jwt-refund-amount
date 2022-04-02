package com.jwt.szs.filter;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jwt.szs.model.dto.member.AuthenticationRequest;
import com.jwt.szs.model.dto.member.MemberResponse;
import com.jwt.szs.model.type.JwtTokenType;
import com.jwt.szs.service.RefreshTokenService;
import com.jwt.szs.service.member.MemberService;
import com.jwt.szs.utils.HttpUtils;
import com.jwt.szs.utils.JwtTokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class CustomAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private MemberService memberService;

    public CustomAuthenticationFilter(String defaultFilterProcessesUrl) {
        super(defaultFilterProcessesUrl);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, ServletException, IOException {

        String body = HttpUtils.getBody(request);

        JsonObject userIdPasswordJson = JsonParser.parseString(body).getAsJsonObject();
        String userId = userIdPasswordJson.get("userId").getAsString();
        String password = userIdPasswordJson.get("password").getAsString();

        AuthenticationRequest authenticationRequest = AuthenticationRequest.builder()
                .userId(userId)
                .password(password)
                .build();
        final MemberResponse memberResponse = memberService.getByUserIdAndPassword(authenticationRequest);

        final String token = JwtTokenUtils.generateToken(memberResponse, JwtTokenType.ACCESS);

        final String refreshJwt = JwtTokenUtils.generateToken(memberResponse, JwtTokenType.REFRESH);

        refreshTokenService.createRefreshToken(refreshJwt, memberResponse.getId(), JwtTokenType.REFRESH.getValidationSeconds());

        request.setAttribute("authenticationToken", token);

        response.addCookie(JwtTokenUtils.createRefreshTokenCookie(refreshJwt));

        return getAuthenticationManager()
                .authenticate(new UsernamePasswordAuthenticationToken(userId, password));
    }
}
