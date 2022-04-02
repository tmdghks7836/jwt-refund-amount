package com.jwt.szs.exception;

import lombok.Getter;


@Getter
public class AlreadyExistException extends CustomRuntimeException {

    public AlreadyExistException() {
        super(ErrorCode.ALREADY_EXIST_DATA);
    }

    public AlreadyExistException(String reason) {
        super(ErrorCode.ALREADY_EXIST_DATA, reason);
    }
}
