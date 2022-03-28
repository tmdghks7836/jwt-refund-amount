package com.jwt.szs.exception;

import lombok.Getter;


@Getter
public class AlreadyDefinedException extends CustomRuntimeException {

    public AlreadyDefinedException() {
        super(ErrorCode.ALREADY_DEFINED);
    }

    public AlreadyDefinedException(String reason) {
        super(ErrorCode.ALREADY_DEFINED, reason);
    }
}
