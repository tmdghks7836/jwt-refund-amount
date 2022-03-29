package com.jwt.szs.exception;

import lombok.Getter;


@Getter
public class MemberNotFoundException extends CustomRuntimeException {

    public MemberNotFoundException(Long id) {
        super(ErrorCode.RESOURCE_NOT_FOUND, new StringBuilder()
                .append("not found member.id : ")
                .append(id.toString()).toString());
    }

    public MemberNotFoundException(String userId) {
        super(ErrorCode.RESOURCE_NOT_FOUND, new StringBuilder()
                .append("not found member.userId : ")
                .append(userId).toString());
    }
}
