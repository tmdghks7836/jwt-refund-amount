package com.jwt.szs.service.member;

import com.jwt.szs.api.codetest3o3.model.NameWithRegNoDto;
import com.jwt.szs.api.service.CodeTest3o3ApiService;
import com.jwt.szs.exception.*;
import com.jwt.szs.model.base.HasUserIdPassword;
import com.jwt.szs.model.dto.EmployeeIncomeResponse;
import com.jwt.szs.model.dto.member.AuthenticationMemberPrinciple;
import com.jwt.szs.model.dto.member.MemberCreationRequest;
import com.jwt.szs.model.dto.member.MemberResponse;
import com.jwt.szs.model.dto.member.UserDetailsImpl;
import com.jwt.szs.model.entity.Member;
import com.jwt.szs.model.mapper.MemberMapper;
import com.jwt.szs.repository.MemberRepository;
import com.jwt.szs.service.EmployeeIncomeService;
import com.jwt.szs.service.callback.MemberCallbackEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

    private final CodeTest3o3ApiService codeTest3o3ApiService;

    private final EmployeeIncomeService employeeIncomeService;

    private final MemberScrapEventService memberScrapEventService;

    private final MemberSignUpEventService memberSignUpEventService;

    private MemberCallbackEvent memberCallbackEvent;

    @Autowired
    public void setMemberCallbackEvent(MemberCallbackEvent memberCallbackEvent) {
        this.memberCallbackEvent = memberCallbackEvent;
    }

    @Override
    public UserDetails loadUserByUsername(final String userId) {

        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException(userId + " not found"));

        return new UserDetailsImpl(member.getUserId(), member.getPassword());
    }

    public MemberResponse getByUserId(String userId) {

        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new MemberNotFoundException(userId));

        return MemberMapper.INSTANCE.modelToDto(member);
    }

    @Transactional
    public void signUp(MemberCreationRequest creationRequest) {

        Optional<Member> memberOptional = memberRepository.findByUserId(creationRequest.getUserId());

        if (memberOptional.isPresent()
                || memberSignUpEventService.didSomeOneRequestPending(creationRequest.getUserId())) {
            throw new AlreadyExistException("이미 존재하는 유저 아이디입니다.");
        }

        Optional<Member> memberByUserIdAndRegNo = memberRepository.findByNameAndRegNo(creationRequest.getName(),
                creationRequest.getRegNo());

        if (memberByUserIdAndRegNo.isPresent()) {
            throw new AlreadyExistException("이미 등록된 유저 정보입니다.");
        }

        memberSignUpEventService.createRequestEvent(creationRequest);

        NameWithRegNoDto nameWithRegNoDto = new NameWithRegNoDto(creationRequest.getName(), creationRequest.getRegNo());

        codeTest3o3ApiService.getScrapByNameAndRegNo(nameWithRegNoDto, memberCallbackEvent.signUpCallback(creationRequest));
    }

    public MemberResponse getById(Long id) {

        Member member = memberRepository.findById(id)
                .orElseThrow(() ->
                        new MemberNotFoundException(id));

        return MemberMapper.INSTANCE.modelToDto(member);
    }

    @Transactional
    public void scrap(String userId) {

        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new MemberNotFoundException(userId));

        memberScrapEventService.createRequestEvent(member.getId());

        NameWithRegNoDto nameWithRegNoDto = NameWithRegNoDto.builder()
                .name(member.getName())
                .regNo(member.getRegNo())
                .build();

        codeTest3o3ApiService.getScrapByNameAndRegNo(nameWithRegNoDto, memberCallbackEvent.getScrapResponseCallback(member.getId()));
    }

    public EmployeeIncomeResponse getRefundInformation(String userId) {

        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new MemberNotFoundException(userId));

        memberScrapEventService.validateHistory(member.getId());

        return employeeIncomeService.getByMember(member);
    }

    @Transactional
    public void createMember(MemberCreationRequest creationRequest) {

        Member member = new Member(
                creationRequest.getUserId(),
                creationRequest.getName(),
                creationRequest.getRegNo(),
                passwordEncoder.encode(creationRequest.getPassword())
        );

        memberRepository.saveAndFlush(member);
        memberSignUpEventService.requestComplete(creationRequest);
    }
}
