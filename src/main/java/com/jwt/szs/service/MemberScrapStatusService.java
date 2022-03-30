package com.jwt.szs.service;

import com.jwt.szs.api.codetest3o3.model.type.ScrapRequestStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 유저 스크랩 저장 요청 상태를 관리합니다.
 * */
@Slf4j
@Service
@RequiredArgsConstructor
public class MemberScrapStatusService {




    public void pending(Long memberId) {

    }

    public Boolean isPending(Long memberId) {

        return getStatus(memberId).equals(ScrapRequestStatus.PENDING);
    }

    public ScrapRequestStatus getStatus(Long memberId) {

        return ScrapRequestStatus.NONE;
    }

    public void requestFailed(Long memberId){

    }
}
