package com.jwt.szs.security.provider;

import com.jwt.szs.model.dto.member.AuthenticationMemberPrinciple;
import com.jwt.szs.model.dto.member.AuthenticationRequest;
import com.jwt.szs.model.dto.member.UserDetailsImpl;
import com.jwt.szs.model.type.JwtTokenType;
import com.jwt.szs.service.RefreshTokenService;
import com.jwt.szs.service.member.MemberService;
import com.jwt.szs.service.member.MemberSignUpEventService;
import com.jwt.szs.utils.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;

    private final PasswordEncoder passwordEncoder;

    private final MemberSignUpEventService memberSignUpEventService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;

        String userId = token.getName();
        String password = (String) token.getCredentials();

        memberSignUpEventService.validateBeforeLogin(userId, password);

        UserDetails userDetails = userDetailsService.loadUserByUsername(userId);

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {

            throw new BadCredentialsException(String.format("%s Invalid password", userDetails.getUsername()));
        }

        final String accessToken = JwtTokenUtils.generateToken(userDetails.getUsername(), JwtTokenType.ACCESS);

        AuthenticationMemberPrinciple authenticationMemberPrinciple = new AuthenticationMemberPrinciple(accessToken);

        return new UsernamePasswordAuthenticationToken(
                authenticationMemberPrinciple, null,
                authenticationMemberPrinciple.getAuthorities()
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

}