package com.jwt.szs.controller;

import com.jwt.szs.controller.advice.ValidateAccessTokenAdvice;
import com.jwt.szs.controller.advice.ValidateTokenRedisAdvice;
import com.jwt.szs.filter.strategy.CheckJwtTokenStrategy;
import com.jwt.szs.model.dto.*;
import com.jwt.szs.model.dto.member.AuthenticationMemberPrinciple;
import com.jwt.szs.model.dto.member.AuthenticationRequest;
import com.jwt.szs.model.dto.member.MemberCreationRequest;
import com.jwt.szs.model.dto.member.MemberResponse;
import com.jwt.szs.model.type.JwtTokenType;
import com.jwt.szs.service.JwtTokenService;
import com.jwt.szs.service.MemberService;
import com.jwt.szs.utils.JwtTokenUtils;
import com.jwt.szs.utils.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.ProxyFactory;
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

    private final RedisUtil redisUtil;

    private final MemberService memberService;

    private final CheckJwtTokenStrategy jwtTokenStrategy;

    private final JwtTokenService jwtTokenService;

    @PostMapping(value = "/login")
    @ApiOperation(value = "로그인")
    public ResponseEntity login(@RequestBody AuthenticationRequest authenticationRequest) {

        log.info("spring security 에서 login 인증기능을 담당합니다.");
        return null;
    }

    @PostMapping("/signup")
    @ApiOperation(value = "회원가입")
    @ResponseStatus(HttpStatus.CREATED)
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
    @ResponseStatus(HttpStatus.OK)
    public IssueTokenResponse accessToken(HttpServletRequest request, HttpServletResponse res) {

        String refreshToken = JwtTokenUtils.findCookie(request, JwtTokenType.REFRESH).getValue();
        String accessToken = jwtTokenStrategy.getTokenByRequest(request);

        ProxyFactory proxyFactory = new ProxyFactory(jwtTokenService); //프록시 팩토리에 원하는 클래스를 주입
        proxyFactory.addAdvice(new ValidateTokenRedisAdvice(redisUtil, refreshToken));
        proxyFactory.addAdvice(new ValidateAccessTokenAdvice(redisUtil, accessToken, refreshToken)); // 공통으로 실행할 advice 객체 주입
        JwtTokenService proxy = (JwtTokenService) proxyFactory.getProxy();

        String reIssuanceAccessToken = proxy.ReIssuanceAccessToken(refreshToken);

        return new IssueTokenResponse(reIssuanceAccessToken);
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
