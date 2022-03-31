package com.jwt.szs.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.jwt.szs.exception.ErrorCode;
import com.jwt.szs.exception.ErrorResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class JwtExceptionFilter extends OncePerRequestFilter {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
        try {  // 앞단 필터에서 에러가 있을 시 이 필터로 넘어온다.
            chain.doFilter(req, res);
        } catch (ExpiredJwtException e) {
            setErrorResponse(ErrorCode.TOKEN_EXPIRED, res, e);
        } catch (JwtException e) {
            setErrorResponse(ErrorCode.VALIDATE_TOKEN_FAILED, res, e);
        }
    }

    public void setErrorResponse(ErrorCode errorCode, HttpServletResponse response, Throwable e) throws IOException {

        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getOutputStream()
                .write(objectMapper.writeValueAsString(
                                ErrorResponse.getByErrorCode(errorCode, e.getMessage())
                        ).getBytes(StandardCharsets.UTF_8)
                );
    }
}