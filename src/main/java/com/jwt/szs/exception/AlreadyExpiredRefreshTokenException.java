package com.jwt.szs.exception;

import lombok.Getter;


@Getter
public class AlreadyExpiredRefreshTokenException extends CustomRuntimeException {

    public AlreadyExpiredRefreshTokenException() {
        super(ErrorCode.TOKEN_EXPIRED,
                "이미 만료된 리프레시토큰입니다.");
    }
}
