package com.jwt.szs.model.dto.member;

import com.jwt.szs.model.base.HasUserIdPassword;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberCreationRequest implements HasUserIdPassword {

    @ApiModelProperty(example = "hong123")
    @NotEmpty
    private String userId;

    @ApiModelProperty(example = "1234")
    @NotEmpty
    private String password;

    @ApiModelProperty(example = "홍길동")
    @NotEmpty
    private String name;

    @ApiModelProperty(example = "860824-1655068")
    @NotEmpty
    @Pattern(regexp = "\\d{2}([0]\\d|[1][0-2])([0][1-9]|[1-2]\\d|[3][0-1])[-][1-4]\\d{6}")
    private String regNo;
}
