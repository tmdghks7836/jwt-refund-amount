package com.jwt.szs.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwt.szs.exception.ErrorCode;
import com.jwt.szs.exception.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 확인된 Authentications exception list
 * InsufficientAuthenticationException 불충분한 인증
 * */
@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        log.info("JwtAuthenticationEntryPoint");
        authException.printStackTrace();
        ErrorCode errorCode = ErrorCode.AUTHENTICATION_FAIL;

        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getOutputStream()
                .write(objectMapper.writeValueAsString(
                                ErrorResponse.getByErrorCode(errorCode, authException.getMessage())
                        ).getBytes(StandardCharsets.UTF_8)
                );
    }
}
