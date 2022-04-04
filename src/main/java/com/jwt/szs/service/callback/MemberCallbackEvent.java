package com.jwt.szs.service.callback;


import com.jwt.szs.api.codetest3o3.model.ScrapResponse;
import com.jwt.szs.core.CustomCallback;
import com.jwt.szs.core.SzsTransactionManager;
import com.jwt.szs.core.SzsTransactionResult;
import com.jwt.szs.model.dto.EmployeeIncomeCreationRequest;
import com.jwt.szs.model.dto.member.MemberCreationRequest;
import com.jwt.szs.service.EmployeeIncomeService;
import com.jwt.szs.service.member.MemberScrapEventService;
import com.jwt.szs.service.member.MemberService;
import com.jwt.szs.service.member.MemberSignUpEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import retrofit2.Call;
import retrofit2.Response;


/**
 * 비동기 호출시 @transactional 동작하지 않음.
 * call.enqueue( new okhttp3.Callback() 으로 작동함
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MemberCallbackEvent {

    private final EmployeeIncomeService employeeIncomeService;

    private final MemberScrapEventService memberScrapEventService;

    private final MemberSignUpEventService memberSignUpEventService;

    private final MemberService memberService;

    private final SzsTransactionManager tm;

    public CustomCallback<ScrapResponse> signUpCallback(MemberCreationRequest creationRequest) {

        return new CustomCallback<ScrapResponse>() {

            @Override
            public void onResponse(Call<ScrapResponse> call, Response<ScrapResponse> response) {
                super.onResponse(call, response);

                ScrapResponse scrapResponse = response.body();

                if (!response.isSuccessful() || scrapResponse.getEmployeeData() == null
                        || scrapResponse.getCalculatedTex() == null || scrapResponse.getIncomeInfo() == null) {
                    /** 회원가입 상태 로그 저장
                     메일 정보가 있다면 회원가입 실패 알람을 보낼 것 같다.
                     * */
                    tm.startTransaction(() ->
                            memberSignUpEventService.requestFailed(creationRequest, "삼쩜삼 api 응답값이 잘못되었습니다."));
                    return;
                }

                ScrapResponse.IncomeInfo incomeInfo = scrapResponse.getIncomeInfo();

                /** 비동기 호출 시
                 * 회원가입진행 시 응답이 올떄까지 프론트에서 일정 초마다 polling check를 하며
                 *  회원가입이 완료될 때까지 기다리는 방법으로 구현 할 수 있을것 같다.. */
                if (!(creationRequest.getName().equals(incomeInfo.getWorkerName())
                        && creationRequest.getRegNo().equals(incomeInfo.getRegNo()))) {

                    tm.startTransaction(() ->
                            memberSignUpEventService.requestFailed(creationRequest,
                                    "삼쩜삼api의 이름과 주민번호가 맞지 않습니다."));
                    return;
                }

                SzsTransactionResult szsTransactionResult = tm.startTransaction(() -> {
                    memberService.createMember(creationRequest);
                });

                if (!szsTransactionResult.getIsSuccess()) {
                    tm.startTransaction(() ->
                            memberSignUpEventService.requestFailed(
                                    creationRequest,
                                    szsTransactionResult.getMessage()
                            )
                    );
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                super.onFailure(call, t);

                tm.startTransaction(() ->
                        memberSignUpEventService.requestFailed(creationRequest,
                                "삼쩜삼 api 응답에 실패했습니다. " + t.getMessage()));
            }
        };
    }

    /**
     * 스크랩 요청 후 응답
     */
    public CustomCallback<ScrapResponse> getScrapResponseCallback(Long memberId) {

        return new CustomCallback<ScrapResponse>() {

            @Override
            public void onResponse(Call<ScrapResponse> call, Response<ScrapResponse> response) {
                super.onResponse(call, response);

                if (!response.isSuccessful()) {

                    log.error("스크랩 요청 실패 memberId : {}, message : {}", memberId, response.message());
                    tm.startTransaction(() -> memberScrapEventService.requestFailed(memberId, response.message()));
                    return;
                }

                ScrapResponse scrapResponse = response.body();
                EmployeeIncomeCreationRequest employeeIncomeCreationRequest = new EmployeeIncomeCreationRequest(scrapResponse);

                SzsTransactionResult szsTransactionResult = tm.startTransaction(() ->
                        employeeIncomeService.upsert(memberId, employeeIncomeCreationRequest));

                if (!szsTransactionResult.getIsSuccess()) {
                    tm.startTransaction(() -> memberScrapEventService.requestFailed(memberId, szsTransactionResult.getMessage()));
                }
            }

            @Override
            public void onFailure(Call<ScrapResponse> call, Throwable t) {
                super.onFailure(call, t);

                log.error("스크랩 요청 실패 memberId : {}, message : {}", memberId, t.getMessage());
                tm.startTransaction(() -> memberScrapEventService.requestFailed(memberId, t.getMessage()));
            }
        };
    }
}
