package com.jwt.szs.service.member;

import com.jwt.szs.api.codetest3o3.model.type.MemberScrapStatus;
import com.jwt.szs.exception.CustomRuntimeException;
import com.jwt.szs.exception.ErrorCode;
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
 */
@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberScrapEventService {

    private final MemberScrapEventRepository memberScrapEventRepository;

    @Transactional
    public void createRequestEvent(Long memberId) {

        MemberScrapEvent memberScrapEvent = new MemberScrapEvent(memberId, MemberScrapStatus.PENDING);

        memberScrapEventRepository.save(memberScrapEvent);
    }

    public void validateHistory(Long memberId) {

        log.info("스크랩 요청 상태 이력을 확인합니다.");

        Optional<MemberScrapEvent> memberScrapEventOptional = get(memberId);

        if (!memberScrapEventOptional.isPresent()) {
            throw new CustomRuntimeException(ErrorCode.NOT_FOUND_REQUEST_HISTORY);
        }

        MemberScrapEvent memberScrapEvent = memberScrapEventOptional.get();

        if (memberScrapEvent.isPending()) {
            throw new CustomRuntimeException(ErrorCode.SCRAP_REQUEST_PENDING);
        }

        if (memberScrapEvent.isFailed()) {
            throw new CustomRuntimeException(ErrorCode.SCRAP_REQUEST_FAILED);
        }
    }

    @Transactional
    public void requestComplete(Long memberId) {

        MemberScrapEvent memberScrapEvent = memberScrapEventRepository.findByMemberIdLately(memberId).orElseThrow(() -> new ResourceNotFoundException());

        memberScrapEvent.complete();
    }

    public Optional<MemberScrapEvent> get(Long memberId) {

        Optional<MemberScrapEvent> scrapEventOptional = memberScrapEventRepository.findByMemberIdLately(memberId);

        return scrapEventOptional;
    }

    public void requestFailed(Long memberId) {

        MemberScrapEvent memberScrapEvent = memberScrapEventRepository.findByMemberIdLately(memberId).orElseThrow(() -> new ResourceNotFoundException());

        memberScrapEvent.failed();
    }
}
