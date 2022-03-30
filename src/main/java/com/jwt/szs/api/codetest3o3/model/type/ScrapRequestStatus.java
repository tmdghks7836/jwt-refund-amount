package com.jwt.szs.api.codetest3o3.model.type;

import com.jwt.szs.exception.ResourceNotFoundException;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ScrapRequestStatus {

    NONE("NONE"),
    PENDING("PENDING"),
    DONE("DONE"),
    FAILED("FAILED");

    private String code;

    ScrapRequestStatus(String code){
        this.code = code;
    }

    public static ScrapRequestStatus findByCode(String code){

        return Arrays.stream(ScrapRequestStatus.values())
                .filter(scrapRequestStatus -> scrapRequestStatus.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("not foubd MemberScrapStatus. code : " + code));
    }
}
