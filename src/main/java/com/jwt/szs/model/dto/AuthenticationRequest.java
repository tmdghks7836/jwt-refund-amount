package com.jwt.szs.model.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationRequest {

    @NotEmpty
    private String userId;

    @NotEmpty
    private String password;
}
