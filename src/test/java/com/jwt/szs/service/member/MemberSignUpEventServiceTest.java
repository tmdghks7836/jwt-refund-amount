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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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

    @Value("${szs.find-sign-up-event-seconds}")
    private Integer findSignUpEventSeconds;

    @Value("${api.test-3o3.timeout}")
    private Integer test3o3ApiTimeout;

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

        MemberSignUpEvent memberSignupEvent = createMemberSignupEvent(MemberSignUpStatus.PENDING);

        when(signUpEventRepository.findByUserIdAfterSeconds(memberSignupEvent.getUserId(), MemberSignUpStatus.COMPLETED, findSignUpEventSeconds))
                .thenReturn(Optional.empty());

        when(signUpEventRepository.findByUserIdAfterSeconds(memberSignupEvent.getUserId(), MemberSignUpStatus.PENDING, test3o3ApiTimeout))
                .thenReturn(Optional.ofNullable(memberSignupEvent));

        CustomRuntimeException customRuntimeException = assertThrows(CustomRuntimeException.class, () ->
                memberSignUpEventService.validateBeforeLogin(createAuthenticationRequest()));

        Assertions.assertEquals(ErrorCode.REQUEST_PENDING, customRuntimeException.getErrorCode());
    }

    @Test
    @DisplayName("로그인 후 특정 시간 내 회원가입 상태 기록 확인 시 같은 아이디로 누군가 먼저 가입한 경우. 패스워드로 체크")
    void validateHistoryInSeconds3() {

        MemberSignUpEvent memberSignupEvent = createMemberSignupEvent(MemberSignUpStatus.FAILED);

        when(signUpEventRepository.findByUserIdAfterSeconds(memberSignupEvent.getUserId(), MemberSignUpStatus.COMPLETED, findSignUpEventSeconds))
                .thenReturn(Optional.empty());

        when(signUpEventRepository.findByUserIdAfterSeconds(memberSignupEvent.getUserId(), MemberSignUpStatus.PENDING, test3o3ApiTimeout))
                .thenReturn(Optional.empty());

        when(signUpEventRepository.findByUserIdAfterSeconds(memberSignupEvent.getUserId(), MemberSignUpStatus.FAILED, findSignUpEventSeconds))
                .thenReturn(Optional.of(memberSignupEvent));

        CustomRuntimeException customRuntimeException = assertThrows(CustomRuntimeException.class, () ->
                memberSignUpEventService.validateBeforeLogin(createAuthenticationRequest()));

        Assertions.assertEquals(ErrorCode.REQUEST_FAILED, customRuntimeException.getErrorCode());
    }

    @Test
    @DisplayName("로그인 후 특정 시간 내 회원가입 상태 기록 확인 시 단순히 비밀번호가 틀렸을때, 회원가입 실패 기록이 없습니다.")
    void validateHistoryInSeconds4() {

        MemberSignUpEvent memberSignUpEvent = createMemberSignupEvent(MemberSignUpStatus.COMPLETED);

        when(signUpEventRepository.findByUserIdAfterSeconds(memberSignUpEvent.getUserId(), MemberSignUpStatus.COMPLETED, findSignUpEventSeconds))
                .thenReturn(Optional.ofNullable(memberSignUpEvent));

        when(signUpEventRepository.findByUserIdAfterSeconds(memberSignUpEvent.getUserId(), MemberSignUpStatus.PENDING, findSignUpEventSeconds))
                .thenReturn(Optional.empty());

        when(signUpEventRepository.findByUserIdAfterSeconds(memberSignUpEvent.getUserId(), MemberSignUpStatus.FAILED, findSignUpEventSeconds))
                .thenReturn(Optional.empty());

        assertDoesNotThrow(() -> memberSignUpEventService.validateBeforeLogin(createAuthenticationRequest()));

    }

    @Test
    @DisplayName("회원가입시 특정 시간 내 누군가 이미 같은 userId로 가입요청을 보냈을 경우")
    void isSomeOneRequestPending() {

        when(signUpEventRepository.findByUserIdAfterSeconds(any(), any(), any()))
                .thenReturn(Optional.ofNullable(createMemberSignupEvent(MemberSignUpStatus.PENDING)));

        AuthenticationRequest authenticationRequest = createAuthenticationRequest();

        boolean someOneRequestPending = memberSignUpEventService.didSomeOneRequestPending(authenticationRequest.getUserId());

        Assertions.assertTrue(someOneRequestPending);
    }

    @Test
    @DisplayName("회원가입시 특정 시간 내 아무도 해당 userId로 가입요청을 보낸 사람이 없을 경우 ")
    void isSomeOneRequestPending2() {

        AuthenticationRequest authenticationRequest = createAuthenticationRequest();

        boolean someOneRequestPending = memberSignUpEventService.didSomeOneRequestPending(authenticationRequest.getUserId());

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