package com.jwt.szs.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    NOT_YET_EXPIRED_TOKEN(HttpStatus.BAD_REQUEST, "E0001", "아직 만료되지 않은 토큰입니다다."),
    NOT_MATCHED_VALUE(HttpStatus.BAD_REQUEST, "E0002", "저장된 값과 일지하지 않습니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "E0003", "이미 만료된 리프레시토큰입니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "E0004", "요청한 리소스를 찾을 수 없습니다."),
    NOT_MATCHED_PASSWORD(HttpStatus.NOT_FOUND, "E0006", "패스워드가 맞지 않습니다."),
    NOT_FOUND_PERMISSION(HttpStatus.FORBIDDEN, "E0007", "권한이 없습니다."),
    AUTHENTICATION_FAIL(HttpStatus.UNAUTHORIZED, "E0008", "인증에 실패하였습니다."),
    ALREADY_EXIST_DATA(HttpStatus.BAD_REQUEST, "E0009", "이미 존재하는 데이터입니다."),
    NOT_MATCHED_PARAM_TYPE(HttpStatus.BAD_REQUEST, "E0010", "파라미터 타입이 맞지 않습니다."),
    ENCRYPTION_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "E0011", "대칭키 암호화에 실패하였습니다."),
    DECRYPTION_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "E0012", "대칭키 복호화에 실패하였습니다."),
    PARSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E0013", "데이터 파싱 에러."),
    SCRAP_REQUEST_PENDING(HttpStatus.BAD_REQUEST, "E0014", "스크랩 요청정보 처리중입니다.");


    private final HttpStatus httpStatus;
    private final String code;
    private final String description;

    public static ErrorCode findByCode(String code) {

        return Arrays.stream(ErrorCode.values()).filter(errorCode -> errorCode.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("not found ErrorCode by {}" + code));
    }
}
