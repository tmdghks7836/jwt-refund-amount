package com.jwt.szs.service.member;

import com.jwt.szs.api.codetest3o3.model.NameWithRegNoDto;
import com.jwt.szs.api.service.CodeTest3o3ApiService;
import com.jwt.szs.exception.*;
import com.jwt.szs.model.dto.EmployeeIncomeResponse;
import com.jwt.szs.model.dto.member.AuthenticationMemberPrinciple;
import com.jwt.szs.model.dto.member.MemberCreationRequest;
import com.jwt.szs.model.dto.member.MemberResponse;
import com.jwt.szs.model.dto.member.UserDetailsImpl;
import com.jwt.szs.model.entity.Member;
import com.jwt.szs.model.mapper.MemberMapper;
import com.jwt.szs.repository.MemberRepository;
import com.jwt.szs.service.EmployeeIncomeService;
import com.jwt.szs.service.event.MemberCallbackEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private final MemberCallbackEvent memberCallbackEvent;


    @Override
    public UserDetails loadUserByUsername(final String userId) {

        Optional<Member> memberOptional = memberRepository.findByUserId(userId);

        if (!memberOptional.isPresent()) {
            throw new RuntimeException();
        }

        Member member = memberOptional.get();
        return new UserDetailsImpl(member.getId(), member.getUserId(), member.getPassword());
    }

    public MemberResponse getByUserId(String userId) {

        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new MemberNotFoundException(userId));

        return MemberMapper.INSTANCE.modelToDto(member);
    }

    public MemberResponse getByUserIdAndPassword(String userId, String password) {

        Optional<Member> memberOptional = memberRepository.findByUserId(userId);

        if (!memberOptional.isPresent()) {
            throw new UsernameNotFoundException(userId + " not found");
        }

        Member member = memberOptional.get();

        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new BadCredentialsException(ErrorCode.NOT_MATCHED_PASSWORD.getDescription());
        }

        return MemberMapper.INSTANCE.modelToDto(member);
    }

    public void asyncSignUp(MemberCreationRequest creationRequest) {

        Optional<Member> memberOptional = memberRepository.findByUserId(creationRequest.getUserId());

        if (memberOptional.isPresent()) {
            throw new AlreadyExistException("이미 존재하는 유저 아이디입니다.");
        }

        NameWithRegNoDto nameWithRegNoDto = new NameWithRegNoDto(creationRequest.getName(), creationRequest.getRegNo());

        log.info("스크랩 정보 검증 후 회원가입을 진행합니다.");

        codeTest3o3ApiService.getScrapByNameAndRegNo(nameWithRegNoDto, memberCallbackEvent.signUpCallback(creationRequest));
    }

    public MemberResponse getById(Long id) {

        Member member = memberRepository.findById(id)
                .orElseThrow(() ->
                        new MemberNotFoundException(id));

        return MemberMapper.INSTANCE.modelToDto(member);
    }

    public void scrap(AuthenticationMemberPrinciple principle) {

        Member member = memberRepository.findById(principle.getId())
                .orElseThrow(() -> new MemberNotFoundException(principle.getId()));

        if (employeeIncomeService.isPresent(member)) {
            throw new AlreadyExistException("스크랩 정보가 이미 존재합니다.");
        }

        if (memberScrapEventService.isPending(member.getId())) {
            throw new CustomRuntimeException(ErrorCode.SCRAP_REQUEST_PENDING);
        }

        memberScrapEventService.pending(member.getId());

        NameWithRegNoDto nameWithRegNoDto = NameWithRegNoDto.builder()
                .name(member.getName())
                .regNo(member.getRegNo())
                .build();

        codeTest3o3ApiService.getScrapByNameAndRegNo(nameWithRegNoDto, memberCallbackEvent.getScrapResponseCallback(member.getId()));
    }

    public EmployeeIncomeResponse getRefundInformation(Long id) {

        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException(id));

        return employeeIncomeService.getByMemberId(member);
    }
}
