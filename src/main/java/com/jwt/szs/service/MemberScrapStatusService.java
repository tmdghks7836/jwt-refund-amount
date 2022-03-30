package com.jwt.szs.service;

import com.jwt.szs.api.codetest3o3.model.type.MemberScrapStatus;
import com.jwt.szs.repository.redis.RedisRepository;
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

    public static final String SAVING_REDIS_KEY = "SCRAP_POST_STATUS_MEMBER_ID";

    public static final Integer SAVING_REDIS_TIME = 1000 * 25;

    public void pending(Long memberId) {

    }

    public Boolean isPending(Long memberId) {

        return getStatus(memberId).equals(MemberScrapStatus.PENDING);
    }

    public MemberScrapStatus getStatus(Long memberId) {

        return MemberScrapStatus.NONE;
    }

    public void requestFailed(Long memberId){

    }
}
