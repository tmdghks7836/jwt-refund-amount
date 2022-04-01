package com.jwt.szs.filter;

import com.jwt.szs.filter.strategy.CheckJwtTokenStrategy;
import com.jwt.szs.model.dto.member.AuthenticationMemberPrinciple;
import com.jwt.szs.utils.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {

    private final CheckJwtTokenStrategy checkJwtTokenStrategy;

    private final String[] excludeUris = {"/szs/token/re-issuance"};

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

        String uri = request.getRequestURI();

        return Arrays.stream(excludeUris).filter(excludeUri -> excludeUri.equals(uri))
                .findFirst()
                .isPresent();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        log.info("authorization check from jwtTokenFilter");

        String token = checkJwtTokenStrategy.getTokenByRequest(request);

        if (JwtTokenUtils.validate(token)) {
            authorization(token);
        }

        chain.doFilter(request, response);
    }

    private void authorization(String token) {

        AuthenticationMemberPrinciple authenticationMemberPrinciple = new AuthenticationMemberPrinciple(token);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                authenticationMemberPrinciple, null,
                authenticationMemberPrinciple.getAuthorities()
        );

//        //TODO 필요한 코드인지?
//        authentication.setDetails(userDetails);

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
