package com.jwt.szs.service.event;


import com.jwt.szs.api.codetest3o3.model.ScrapResponse;
import com.jwt.szs.core.CustomCallback;
import com.jwt.szs.exception.MemberNotFoundException;
import com.jwt.szs.model.dto.EmployeeIncomeCreationRequest;
import com.jwt.szs.model.dto.member.MemberCreationRequest;
import com.jwt.szs.model.entity.Member;
import com.jwt.szs.repository.MemberRepository;
import com.jwt.szs.service.EmployeeIncomeService;
import com.jwt.szs.service.member.MemberScrapEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import retrofit2.Call;
import retrofit2.Response;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberCallbackEvent {

    private final PasswordEncoder passwordEncoder;

    private final MemberRepository memberRepository;

    private final EmployeeIncomeService employeeIncomeService;

    private final MemberScrapEventService memberScrapEventService;

    @Transactional
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

                /** 비동기 호출 시
                 * 회원가입 성공 후 프론트에서 일정 초마다 polling check를 하며
                 *  회원가입이 완료될 때까지 기다리는 방법으로 구현 할 수 있을것 같다.. */
                if (creationRequest.getName().equals(workerName)
                        && creationRequest.getRegNo().equals(regNo)) {

                    Member member = new Member(
                            creationRequest.getUserId(),
                            creationRequest.getName(),
                            creationRequest.getRegNo(),
                            encodedPassword
                    );

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

    public CustomCallback<ScrapResponse> getScrapResponseCallback(Long memberId) {

        return new CustomCallback<ScrapResponse>() {

            @Override
            public void onResponse(Call<ScrapResponse> call, Response<ScrapResponse> response) {
                super.onResponse(call, response);

                ScrapResponse scrapResponse = response.body();
                EmployeeIncomeCreationRequest employeeIncomeCreationRequest = new EmployeeIncomeCreationRequest(scrapResponse);

                Member member = memberRepository.findById(memberId)
                        .orElseThrow(() -> new MemberNotFoundException(memberId));

                employeeIncomeService.create(member, employeeIncomeCreationRequest);
            }

            @Override
            public void onFailure(Call<ScrapResponse> call, Throwable t) {
                super.onFailure(call, t);

                memberScrapEventService.requestFailed(memberId);
            }
        };
    }

}
