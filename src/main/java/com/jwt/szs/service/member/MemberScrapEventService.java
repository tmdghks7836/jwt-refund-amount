package com.jwt.szs.service.member;

import com.jwt.szs.api.codetest3o3.model.type.MemberScrapStatus;
import com.jwt.szs.exception.CustomRuntimeException;
import com.jwt.szs.exception.ErrorCode;
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

        Optional<MemberScrapEvent> memberScrapEventOptional = getByCreatedAtDesc(memberId);

        if (!memberScrapEventOptional.isPresent()) {
            throw new CustomRuntimeException(ErrorCode.NOT_FOUND_REQUEST_HISTORY);
        }

        MemberScrapEvent memberScrapEvent = memberScrapEventOptional.get();

        if (memberScrapEvent.isPending()) {
            throw new CustomRuntimeException(ErrorCode.REQUEST_PENDING);
        }

        if (memberScrapEvent.isFailed()) {
            throw new CustomRuntimeException(ErrorCode.REQUEST_FAILED, "스크랩에 실패하였습니다.");
        }
    }

    public void requestComplete(Long memberId) {

        MemberScrapEvent memberScrapEvent = new MemberScrapEvent(memberId, MemberScrapStatus.COMPLETED);

        memberScrapEventRepository.saveAndFlush(memberScrapEvent);
    }

    public Optional<MemberScrapEvent> getByCreatedAtDesc(Long memberId) {

        Optional<MemberScrapEvent> scrapEventOptional = memberScrapEventRepository.findByMemberIdAndCreatedAtDesc(memberId);

        return scrapEventOptional;
    }

    /**
     * 비동기 호출 시 @transactional 기능이 동작하지 않아 flush로 대체
     */
    public void requestFailed(Long memberId, String message) {

        MemberScrapEvent memberScrapEvent = new MemberScrapEvent(memberId, MemberScrapStatus.FAILED, message);

        memberScrapEventRepository.saveAndFlush(memberScrapEvent);
    }
}
