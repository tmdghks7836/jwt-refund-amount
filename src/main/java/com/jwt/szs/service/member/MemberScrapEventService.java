package com.jwt.szs.service.member;

import com.jwt.szs.api.codetest3o3.model.type.MemberScrapStatus;
import com.jwt.szs.exception.ResourceNotFoundException;
import com.jwt.szs.model.entity.MemberScrapEvent;
import com.jwt.szs.repository.MemberScrapEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 유저 스크랩 저장 요청 상태를 관리합니다.
 * */
@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberScrapEventService {

    private final MemberScrapEventRepository memberScrapEventRepository;

    @Transactional
    public void pending(Long memberId) {

        MemberScrapEvent memberScrapEvent = new MemberScrapEvent(memberId, MemberScrapStatus.PENDING);

        memberScrapEventRepository.save(memberScrapEvent);
    }

    public Boolean isPending(Long memberId) {

        return getStatus(memberId).equals(MemberScrapStatus.PENDING);
    }

    public MemberScrapStatus getStatus(Long memberId) {

        MemberScrapEvent memberScrapEvent = memberScrapEventRepository.findByMemberIdLately(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 멤버의 스크랩 상태정보가 없습니다."));

        return memberScrapEvent.getStatus();
    }

    public void requestFailed(Long memberId){

    }
}
