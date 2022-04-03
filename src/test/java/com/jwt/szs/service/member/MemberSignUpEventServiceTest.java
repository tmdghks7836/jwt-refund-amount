package com.jwt.szs.service.member;

import com.jwt.szs.api.codetest3o3.model.type.MemberSignUpStatus;
import com.jwt.szs.exception.CustomRuntimeException;
import com.jwt.szs.exception.ErrorCode;
import com.jwt.szs.model.dto.member.AuthenticationRequest;
import com.jwt.szs.model.entity.MemberSignUpEvent;
import com.jwt.szs.repository.MemberSignUpEventRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MemberSignUpEventServiceTest {


    @Autowired
    MemberSignUpEventService memberSignUpEventService;

    @MockBean
    MemberSignUpEventRepository signUpEventRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    private final String userId = "hong123";
    private final String password = "123";

    @Test
    @DisplayName("회원가입 요청 이벤트 생성")
    void createRequestEvent() {

        Assertions.assertDoesNotThrow(() ->
                memberSignUpEventService.createRequestEvent(createAuthenticationRequest()));
    }

    @Test
    @DisplayName("회원가입 완료 이벤트 생성")
    void requestComplete() {

        Assertions.assertDoesNotThrow(() ->
                memberSignUpEventService.requestComplete(createAuthenticationRequest()));
    }

    @Test
    @DisplayName("회원가입 실패 이벤트 생성")
    void requestFailed() {

        Assertions.assertDoesNotThrow(() ->
                memberSignUpEventService.requestFailed(createAuthenticationRequest(), "실패"));
    }

    @Test
    @DisplayName("로그인 시 특정 시간 내 회원가입 상태 기록없으므로 예외처리 하지 않음.")
    void validateHistoryInSeconds() {

        when(signUpEventRepository.findByUserIdAfterSeconds(any(), any(), any()))
                .thenReturn(Optional.empty());

        Assertions.assertDoesNotThrow(() ->
                memberSignUpEventService.validateBeforeLogin(createAuthenticationRequest()));
    }

    @Test
    @DisplayName("로그인 시 특정 시간 내 회원가입 상태 기록 확인 시 회원가입 요청중일 경우.")
    void validateHistoryInSeconds2() {

        when(signUpEventRepository.findByUserIdAfterSeconds(any(), any(), any()))
                .thenReturn(Optional.ofNullable(createMemberSignupEvent(MemberSignUpStatus.PENDING)));

        CustomRuntimeException customRuntimeException = assertThrows(CustomRuntimeException.class, () ->
                memberSignUpEventService.validateBeforeLogin(createAuthenticationRequest()));

        Assertions.assertEquals(ErrorCode.REQUEST_PENDING, customRuntimeException.getErrorCode());
    }

    @Test
    @DisplayName("로그인 시 특정 시간 내 회원가입 상태 기록 확인 시 회원가입 요청 실패날 경우.")
    void validateHistoryInSeconds3() {

        when(signUpEventRepository.findByUserIdAfterSeconds(any(), any(), any()))
                .thenReturn(Optional.ofNullable(createMemberSignupEvent(MemberSignUpStatus.FAILED)));

        CustomRuntimeException customRuntimeException = assertThrows(CustomRuntimeException.class, () ->
                memberSignUpEventService.validateBeforeLogin(createAuthenticationRequest()));

        Assertions.assertEquals(ErrorCode.REQUEST_FAILED, customRuntimeException.getErrorCode());
    }

    @Test
    @DisplayName("회원가입시 특정 시간 내 누군가 이미 같은 userId로 가입요청을 보냈을 경우")
    void isSomeOneRequestPending() {

        when(signUpEventRepository.findByUserIdAfterSeconds(any(), any(), any()))
                .thenReturn(Optional.ofNullable(createMemberSignupEvent(MemberSignUpStatus.PENDING)));


        boolean someOneRequestPending = memberSignUpEventService.isSomeOneRequestPending(createAuthenticationRequest());

        Assertions.assertTrue(someOneRequestPending);
    }

    @Test
    @DisplayName("회원가입시 특정 시간 내 아무도 해당 userId로 가입요청을 보낸 사람이 없을 경우 ")
    void isSomeOneRequestPending2() {

        boolean someOneRequestPending = memberSignUpEventService.isSomeOneRequestPending(createAuthenticationRequest());

        Assertions.assertFalse(someOneRequestPending);
    }


    AuthenticationRequest createAuthenticationRequest() {
        return AuthenticationRequest.builder()
                .userId(userId)
                .password(password).build();
    }

    MemberSignUpEvent createMemberSignupEvent(MemberSignUpStatus status) {

        return new MemberSignUpEvent(
                userId,
                passwordEncoder.encode(password),
                status);
    }

}