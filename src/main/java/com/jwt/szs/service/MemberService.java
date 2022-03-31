package com.jwt.szs.service;

import com.jwt.szs.api.codetest3o3.model.NameWithRegNoDto;
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

    private final MemberScrapStatusService memberScrapStatusService;


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
    public void signUp(MemberCreationRequest creationRequest) {

        Optional<Member> memberOptional = memberRepository.findByUserId(creationRequest.getUserId());

        if (memberOptional.isPresent()) {
            throw new AlreadyExistException("이미 존재하는 유저 아이디입니다.");
        }

        NameWithRegNoDto nameWithRegNoDto = new NameWithRegNoDto(creationRequest.getName(), creationRequest.getRegNo());

        codeTest3o3ApiService.getScrapByNameAndRegNo(nameWithRegNoDto, signUpCallback(creationRequest));
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

        if (employeeIncomeService.isPresent(member)) {
            throw new AlreadyExistException("스크랩 정보가 이미 존재합니다.");
        }

        if (memberScrapStatusService.isPending(member.getId())) {
            throw new CustomRuntimeException(ErrorCode.SCRAP_REQUEST_PENDING);
        }

        memberScrapStatusService.pending(member.getId());

        NameWithRegNoDto nameWithRegNoDto = NameWithRegNoDto.builder()
                .name(member.getName())
                .regNo(member.getRegNo())
                .build();

        codeTest3o3ApiService.getScrapByNameAndRegNo(nameWithRegNoDto, getScrapResponseCallback(member.getId()));
    }

    /**
     * 유저 근로 소득 정보 불러오기
     */
    public void createEmployeeIncome(Long memberId, EmployeeIncomeCreationRequest employeeIncomeCreationRequest) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("notFound member by memberId " + memberId));

        employeeIncomeService.create(member, employeeIncomeCreationRequest);
    }

    //TODO 다른 Class 에 해당 메서드의 책임을 부여해야 하는지?
    private CustomCallback<ScrapResponse> getScrapResponseCallback(Long memberId) {

        return new CustomCallback<ScrapResponse>() {

            @Override
            public void onResponse(Call<ScrapResponse> call, Response<ScrapResponse> response) {
                super.onResponse(call, response);

                ScrapResponse scrapResponse = response.body();
                EmployeeIncomeCreationRequest employeeIncomeCreationRequest = new EmployeeIncomeCreationRequest(scrapResponse);

                createEmployeeIncome(memberId, employeeIncomeCreationRequest);
            }

            @Override
            public void onFailure(Call<ScrapResponse> call, Throwable t) {
                super.onFailure(call, t);

                memberScrapStatusService.requestFailed(memberId);
            }
        };
    }

    public CustomCallback<ScrapResponse> signUpCallback(MemberCreationRequest creationRequest) {

        return new CustomCallback<ScrapResponse>() {

            @Override
            public void onResponse(Call<ScrapResponse> call, Response<ScrapResponse> response) {
                super.onResponse(call, response);

                ScrapResponse scrapResponse = response.body();

                if (!response.isSuccessful() || scrapResponse.getEmployeeData() == null
                        || scrapResponse.getCalculatedTex() == null || scrapResponse.getIncomeInfo() == null) {
                    /*TODO 회원가입 상태 로그 저장
                        메일 정보가 있다면 회원가입 실패 알람을 보낼 것 같다.
                    * */
                    return;
                }

                String workerName = scrapResponse.getIncomeInfo().getWorkerName();
                String regNo = scrapResponse.getIncomeInfo().getRegNo();
                String encodedPassword = passwordEncoder.encode(creationRequest.getPassword());

                Member member = new Member(
                        creationRequest.getUserId(),
                        creationRequest.getName(),
                        creationRequest.getRegNo(),
                        encodedPassword
                );

                /** 비동기 호출 시
                 * 회원가입 성공 후 프론트에서 일정 초마다 polling check를 하며
                 *  회원가입이 완료될 때까지 기다리는 방법으로 구현 할 수 있을것 같다.. */
                if (member.getName().equals(workerName) && member.getRegNo().equals(regNo)) {

                    memberRepository.save(member);
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                super.onFailure(call, t);

                //TODO 회원가입 상태 로그 저장
                return;
            }
        };
    }

    public EmployeeIncomeResponse getRefundInformation(Long id) {

        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException(id));

        return employeeIncomeService.getByMember(member);
    }
}
