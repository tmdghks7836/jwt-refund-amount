package com.jwt.szs.model.dto;

import com.jwt.szs.model.base.BaseMember;
import com.jwt.szs.utils.JwtTokenUtils;
import com.querydsl.core.util.ArrayUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.Collections;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthenticationMemberPrinciple implements BaseMember {

    private String token;

    //인가 플로우일때 생성
    public AuthenticationMemberPrinciple(String token) {
        this.token = token;
    }

    public List<GrantedAuthority> getAuthorities() {

        String[] roles = JwtTokenUtils.getRoles(token);

        if (ArrayUtils.isEmpty(roles)) {
            return Collections.emptyList();
        }
        return AuthorityUtils.createAuthorityList(roles);
    }

    public Long getId() {
        return JwtTokenUtils.getId(token);
    }

    public String getUserId() {
        return JwtTokenUtils.getUserId(token);
    }

    public String getName() {
        return JwtTokenUtils.getName(token);
    }
}
