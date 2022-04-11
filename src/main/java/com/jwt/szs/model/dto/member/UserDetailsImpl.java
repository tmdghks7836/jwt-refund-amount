package com.jwt.szs.model.dto.member;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserDetailsImpl implements UserDetails {

    private String username;

    private String password;

    private List<GrantedAuthority> authorities = new ArrayList<>();

    private boolean enabled = true;

    //인증 플로우일때 생성
    public UserDetailsImpl(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return enabled;
    }

    @Override
    public boolean isAccountNonLocked() {
        return enabled;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return enabled;
    }
}
