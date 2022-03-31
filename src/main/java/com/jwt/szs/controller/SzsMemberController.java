package com.jwt.szs.controller;

import com.jwt.szs.filter.ReIssuanceAccessTokenFilter;
import com.jwt.szs.model.dto.EmployeeIncomeResponse;
import com.jwt.szs.model.dto.member.AuthenticationMemberPrinciple;
import com.jwt.szs.model.dto.member.AuthenticationRequest;
import com.jwt.szs.model.dto.member.MemberCreationRequest;
import com.jwt.szs.model.dto.member.MemberResponse;
import com.jwt.szs.service.MemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Slf4j
@RestController
@Api(tags = "유저")
@RequiredArgsConstructor
@RequestMapping("/szs")
public class SzsMemberController {

    private final MemberService memberService;

    @PostMapping(value = "/login")
    @ApiOperation(value = "로그인")
    public ResponseEntity login(@RequestBody AuthenticationRequest authenticationRequest) {

        log.info("spring security 에서 login 인증기능을 담당합니다.");
        return null;
    }

    @PostMapping("/signup")
    @ApiOperation(value = "회원가입")
    @ResponseStatus(HttpStatus.OK)
    public void signUp(@RequestBody @Valid MemberCreationRequest memberCreationRequest) {

        memberService.signUp(memberCreationRequest);
    }

    @GetMapping("/me")
    @ApiOperation(value = "내 정보 보기")
    @ResponseStatus(HttpStatus.OK)
    public MemberResponse me(@AuthenticationPrincipal AuthenticationMemberPrinciple principle) {

        return memberService.getByUserId(principle.getUserId());
    }

    @GetMapping(value = "/token/re-issuance")
    @ApiOperation(value = "access token 재발급")
    public void reIssuanceToken() {

        log.info("ReIssuanceAccessTokenFilter 에서 액세스 토큰 재발급을 진행합니다. {}", ReIssuanceAccessTokenFilter.class);
    }

    @PostMapping("/scrap")
    @ApiOperation(value = "유저 정보 스크랩")
    @ResponseStatus(HttpStatus.OK)
    public void scrapMyInfo(@AuthenticationPrincipal AuthenticationMemberPrinciple principle) {

        memberService.scrap(principle);
    }

    @GetMapping("/refund")
    @ApiOperation(value = "유저 환급액 계산 정보")
    @ResponseStatus(HttpStatus.OK)
    public EmployeeIncomeResponse refund(@AuthenticationPrincipal AuthenticationMemberPrinciple principle) {

        return memberService.getRefundInformation(principle.getId());
    }
}
