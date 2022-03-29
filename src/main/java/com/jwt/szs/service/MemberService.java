package com.jwt.szs.service;

import com.jwt.szs.api.codetest3o3.model.ScrapRequest;
import com.jwt.szs.api.codetest3o3.model.ScrapResponse;
import com.jwt.szs.api.service.CodeTest3o3ApiService;
import com.jwt.szs.core.CustomCallback;
import com.jwt.szs.exception.*;
import com.jwt.szs.model.dto.*;
import com.jwt.szs.model.dto.member.AuthenticationMemberPrinciple;
import com.jwt.szs.model.dto.member.MemberCreationRequest;
import com.jwt.szs.model.dto.member.MemberResponse;
import com.jwt.szs.model.dto.member.UserDetailsImpl;
import com.jwt.szs.model.entity.Member;
import com.jwt.szs.model.mapper.MemberMapper;
import com.jwt.szs.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import retrofit2.Call;
import retrofit2.Response;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

    private final CodeTest3o3ApiService codeTest3o3ApiService;

    private final EmployeeIncomeService employeeIncomeService;

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

    @Transactional
    public Long signUp(MemberCreationRequest creationRequest) {

        Optional<Member> memberOptional = memberRepository.findByUserId(creationRequest.getUserId());

        if (memberOptional.isPresent()) {
            throw new AlreadyDefinedException("이미 존재하는 유저 아이디입니다.");
        }

        String encodedPassword = passwordEncoder.encode(creationRequest.getPassword());
        Member member = new Member(
                creationRequest.getUserId(),
                creationRequest.getName(),
                creationRequest.getRegNo(),
                encodedPassword
        );

        memberRepository.save(member);

        return member.getId();
    }

    public MemberResponse getById(Long id) {

        Member member = memberRepository.findById(id)
                .orElseThrow(() ->
                        new MemberNotFoundException(id));

        return MemberMapper.INSTANCE.modelToDto(member);
    }

    public void scrap(AuthenticationMemberPrinciple principle) {

        Member member = memberRepository.findById(principle.getId())
                .orElseThrow(() -> new ResourceNotFoundException());

        ScrapRequest scrapRequest = ScrapRequest.builder()
                .name(member.getName())
                .regNo(member.getRegNo())
                .build();

        codeTest3o3ApiService.getScrapByNameAndRegNo(scrapRequest, getScrapResponseCallback(member.getId()));
    }

    public void createEmployeeIncome(Long memberId, EmployeeIncomeCreationRequest employeeIncomeCreationRequest) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("notFound member by memberId " + memberId));

        employeeIncomeService.create(member, employeeIncomeCreationRequest);
    }

    //TODO 다른 객체에 책임을 부여해야 하는지?
    private CustomCallback<ScrapResponse> getScrapResponseCallback(Long memberId) {

        return new CustomCallback<ScrapResponse>() {

            @Override
            public void onResponse(Call<ScrapResponse> call, Response<ScrapResponse> response) {
                super.onResponse(call, response);

                //TODO 실패시 멤버 스크랩 상태값을 변경해야함.
                if (!response.isSuccessful()) {

                }

                ScrapResponse scrapResponse = response.body();
                EmployeeIncomeCreationRequest employeeIncomeCreationRequest = new EmployeeIncomeCreationRequest(scrapResponse);

                createEmployeeIncome(memberId, employeeIncomeCreationRequest);
            }

            @Override
            public void onFailure(Call<ScrapResponse> call, Throwable t) {
                super.onFailure(call, t);
                //TODO 멤버 스크랩 상태값 실패로 변경
            }
        };
    }


    public EmployeeIncomeResponse getRefundInformation(Long id) {

        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException(id));

        return employeeIncomeService.getRefund(member);
    }
}
