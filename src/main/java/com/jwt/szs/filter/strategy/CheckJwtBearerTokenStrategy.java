package com.jwt.szs.filter.strategy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwt.szs.model.dto.jwt.IssueTokenResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;

/**
 * header의 Authorization : Bearer token 으로 검증합니다.
 */
public class CheckJwtBearerTokenStrategy implements CheckJwtTokenStrategy {

    @Override
    public void setResponseToken(HttpServletResponse response, String token) {

        try {

            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getOutputStream()
                    .write(
                            new ObjectMapper().writeValueAsString(
                                    new IssueTokenResponse(token)).getBytes(StandardCharsets.UTF_8)
                    );

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public String getTokenByRequest(HttpServletRequest request) {

        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (!StringUtils.hasText(header) || !header.startsWith("Bearer ")) {
            return null;
        }

        final String token = header.split(" ")[1].trim();

        return token;
    }
}
