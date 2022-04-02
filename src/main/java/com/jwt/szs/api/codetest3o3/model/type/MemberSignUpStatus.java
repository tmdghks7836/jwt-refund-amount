package com.jwt.szs.api.codetest3o3.model.type;

import com.jwt.szs.exception.ResourceNotFoundException;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum MemberSignUpStatus {

    PENDING("PENDING"),
    COMPLETED("COMPLETED"),
    FAILED("FAILED");

    private String code;

    MemberSignUpStatus(String code){
        this.code = code;
    }

    public static MemberSignUpStatus findByCode(String code){

        return Arrays.stream(MemberSignUpStatus.values())
                .filter(scrapRequestStatus -> scrapRequestStatus.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("not found member signUp status. code : " + code));
    }
}
