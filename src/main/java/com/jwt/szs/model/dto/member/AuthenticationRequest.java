package com.jwt.szs.model.dto.member;

import com.jwt.szs.model.base.HasUserIdPassword;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationRequest implements HasUserIdPassword {

    @ApiModelProperty(example = "hong123")
    @NotEmpty
    private String userId;

    @ApiModelProperty(example = "1234")
    @NotEmpty
    private String password;
}
