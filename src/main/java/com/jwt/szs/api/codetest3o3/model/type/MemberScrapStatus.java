package com.jwt.szs.api.codetest3o3.model.type;

import com.jwt.szs.exception.ResourceNotFoundException;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum MemberScrapStatus {

    NONE("NONE"),
    PENDING("PENDING"),
    DONE("DONE"),
    FAILED("FAILED");

    private String code;

    MemberScrapStatus(String code){
        this.code = code;
    }

    public static MemberScrapStatus findByCode(String code){

        return Arrays.stream(MemberScrapStatus.values())
                .filter(memberScrapStatus -> memberScrapStatus.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("not foubd MemberScrapStatus. code : " + code));
    }
}
