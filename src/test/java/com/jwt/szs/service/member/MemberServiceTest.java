package com.jwt.szs.service.member;

import com.jwt.szs.exception.AlreadyExistException;
import com.jwt.szs.exception.CustomRuntimeException;
import com.jwt.szs.exception.ErrorCode;
import com.jwt.szs.exception.MemberNotFoundException;
import com.jwt.szs.model.base.BaseMember;
import com.jwt.szs.model.dto.member.*;
import com.jwt.szs.model.entity.Member;
import com.jwt.szs.model.type.JwtTokenType;
import com.jwt.szs.repository.MemberRepository;
import com.jwt.szs.repository.MemberScrapEventRepository;
import com.jwt.szs.utils.JwtTokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;


@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MemberServiceTest {

    @MockBean
    private MemberRepository memberRepository;

    @MockBean
    private MemberSignUpEventService memberSignUpEventService;

    @MockBean
    private MemberScrapEventRepository memberScrapEventRepository;

    @Autowired
    private MemberService memberService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final String userId = "hong123";
    private final String name = "홍길동";
    private final String password = "123";
    private final String regNo = "860824-1655068";

    @Test
    void loadUserByUsername() {

        Mockito.when(memberRepository.findByUserId(any()))
                .thenReturn(Optional.ofNullable(createMember()));


        UserDetails userDetails = memberService.loadUserByUsername(userId);
        assertEquals(userId, userDetails.getUsername());
    }

    @Test
    void getByUserId() {

        Member member = createMember();

        Mockito.when(memberRepository.findByUserId(any()))
                .thenReturn(Optional.ofNullable(member));

        MemberResponse memberResponse = memberService.getByUserId(member.getUserId());

        assertEquals(member.getUserId(), memberResponse.getUserId());
        assertEquals(name, memberResponse.getName());
        assertEquals(regNo, memberResponse.getRegNo());
    }

    @Test
    void 회원가입() {

        memberService.signUp(createMemberCreationRequest());
    }

    @Test
    @DisplayName("회원가입 실패. 가입하려는 아이디가 이미 존재할때")
    void signupThrowAlreadyExistException() {

        Mockito.when(memberRepository.findByUserId(any()))
                .thenReturn(Optional.ofNullable(createMember()));

        Assertions.assertThrows(AlreadyExistException.class,
                () -> memberService.signUp(createMemberCreationRequest()));
    }

    @Test
    @DisplayName("회원가입 실패. 누군가 회원가입 요청 중.")
    void signupThrowAlreadyExistException2() {

        Mockito.when(memberSignUpEventService.didSomeOneRequestPending(any()))
                .thenReturn(true);


        Assertions.assertThrows(AlreadyExistException.class,
                () -> memberService.signUp(createMemberCreationRequest()));
    }

    @Test
    void getById() {

        Mockito.when(memberRepository.findById(any()))
                .thenReturn(Optional.ofNullable(createMember()));

        MemberResponse memberResponse = memberService.getById(any());
        assertEquals(userId, memberResponse.getUserId());
        assertEquals(name, memberResponse.getName());
        assertEquals(regNo, memberResponse.getRegNo());
    }

    @Test
    @DisplayName("회원가입 실패. 멤버를 찾을 수 없음.")
    void scrapFailed() {

        Mockito.when(memberRepository.findByUserId(any()))
                .thenReturn(Optional.ofNullable(null));

        Mockito.when(memberScrapEventRepository.save(any()))
                .thenReturn(any());

        Assertions.assertThrows(MemberNotFoundException.class, () ->
                memberService.scrap(userId));
    }

    @Test
    void scrap() {

        Mockito.when(memberRepository.findByUserId(any()))
                .thenReturn(Optional.ofNullable(createMember()));

        Mockito.when(memberScrapEventRepository.save(any()))
                .thenReturn(any());

        memberService.scrap(userId);
    }

    @Test
    @DisplayName("환급 요청 이력이 없습니다..")
    void getRefundInformationFailed() {

        Mockito.when(memberRepository.findByUserId(any()))
                .thenReturn(Optional.ofNullable(createMember()));

        CustomRuntimeException customRuntimeException = assertThrows(CustomRuntimeException.class, () ->
                memberService.getRefundInformation(any()));

        Assertions.assertEquals(customRuntimeException.getErrorCode(), ErrorCode.NOT_FOUND_REQUEST_HISTORY);

    }

    public Member createMember() {
        return new Member(userId, name, regNo, passwordEncoder.encode(password));
    }

    MemberCreationRequest createMemberCreationRequest() {
        return MemberCreationRequest.builder()
                .userId(userId)
                .password(password)
                .name(name)
                .regNo(regNo).build();
    }

}