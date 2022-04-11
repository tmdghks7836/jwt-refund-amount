package com.jwt.szs.security.filter;

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

    /**
     * attemptAuthentication에서는 간단한 userid password 추출 기능을 담당하고
     * 인증 관련 부분은 providerManager 에 등록된 AuthenticationProvider 에 책임을 전가.
     * */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, ServletException, IOException {

        String body = HttpUtils.getBody(request);

        JsonObject userIdPasswordJson = JsonParser.parseString(body).getAsJsonObject();
        String userId = userIdPasswordJson.get("userId").getAsString();
        String password = userIdPasswordJson.get("password").getAsString();

        return getAuthenticationManager()
                .authenticate(new UsernamePasswordAuthenticationToken(userId, password));
    }
}
