package com.jwt.szs.service.member;

import com.jwt.szs.api.codetest3o3.model.type.MemberScrapStatus;
import com.jwt.szs.exception.CustomRuntimeException;
import com.jwt.szs.exception.ErrorCode;
import com.jwt.szs.model.entity.MemberScrapEvent;
import com.jwt.szs.repository.MemberScrapEventRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MemberScrapEventServiceTest {

    @Autowired
    MemberScrapEventService memberScrapEventService;

    @MockBean
    MemberScrapEventRepository memberScrapEventRepository;

    @Test
    @DisplayName("스크랩 상태이력 검증")
    void validateHistory() {

        Mockito.when(memberScrapEventRepository.findByMemberIdAndCreatedAtDesc(any()))
                .thenReturn(getMemberScrapEvent(MemberScrapStatus.COMPLETED));

        Assertions.assertDoesNotThrow(() -> memberScrapEventService.validateHistory(anyLong()));
    }

    @Test
    @DisplayName("스크랩 상태이력 검증. 이전에 요청한 이력이 없을 경우.")
    void validateHistory2() {

        Mockito.when(memberScrapEventRepository.findByMemberIdAndCreatedAtDesc(any()))
                .thenReturn(Optional.empty());

        CustomRuntimeException customRuntimeException = assertThrows(CustomRuntimeException.class
                , () -> memberScrapEventService.validateHistory(anyLong()));

        Assertions.assertEquals(ErrorCode.NOT_FOUND_REQUEST_HISTORY ,customRuntimeException.getErrorCode());
    }

    @Test
    @DisplayName("스크랩 상태이력 검증. 요청 중일떄.")
    void validateHistory3() {

        Mockito.when(memberScrapEventRepository.findByMemberIdAndCreatedAtDesc(any()))
                .thenReturn(getMemberScrapEvent(MemberScrapStatus.PENDING));

        CustomRuntimeException customRuntimeException = assertThrows(CustomRuntimeException.class
                , () -> memberScrapEventService.validateHistory(anyLong()));

        Assertions.assertEquals(ErrorCode.REQUEST_PENDING ,customRuntimeException.getErrorCode());
    }

    @Test
    @DisplayName("스크랩 상태이력 검증. 알수 없는 이유로 스크랩 실패.")
    void validateHistory4() {

        Mockito.when(memberScrapEventRepository.findByMemberIdAndCreatedAtDesc(any()))
                .thenReturn(getMemberScrapEvent(MemberScrapStatus.FAILED));

        CustomRuntimeException customRuntimeException = assertThrows(CustomRuntimeException.class
                , () -> memberScrapEventService.validateHistory(anyLong()));

        Assertions.assertEquals(ErrorCode.REQUEST_FAILED ,customRuntimeException.getErrorCode());
    }

    Optional<MemberScrapEvent> getMemberScrapEvent(MemberScrapStatus status) {

        return Optional.ofNullable(new MemberScrapEvent(1l, status));
    }
}