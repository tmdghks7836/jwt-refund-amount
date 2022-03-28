package com.jwt.szs.model.dto;

import com.jwt.szs.model.base.BaseMember;
import com.jwt.szs.model.entity.Member;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponse implements BaseMember {

    private Long id;

    private String userId;

    private String name;

    private String regNo;
}
