package com.jwt.szs.model.dto.member;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotEmpty;

/**
 * 해당 객체는 로그인 기능이 security 로 책임이 부여되면서 더이상 사용하지 않습니다.
 * swagger example value를 위해 남겨놓았습니다.
 * */
@Deprecated
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationRequest {

    @ApiModelProperty(example = "hone123")
    @NotEmpty
    private String userId;

    @ApiModelProperty(example = "1234")
    @NotEmpty
    private String password;
}
