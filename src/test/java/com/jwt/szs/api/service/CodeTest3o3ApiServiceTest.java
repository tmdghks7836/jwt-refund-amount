package com.jwt.szs.api.service;

import com.jwt.szs.api.codetest3o3.model.ScrapRequest;
import com.jwt.szs.api.codetest3o3.model.ScrapResponse;
import com.jwt.szs.core.CustomCallback;
import com.jwt.szs.model.dto.member.MemberResponse;
import com.jwt.szs.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import retrofit2.Call;
import retrofit2.Response;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CodeTest3o3ApiServiceTest {

    @Autowired
    private CodeTest3o3ApiService codeTest3o3ApiService;

    @MockBean
    private MemberService memberService;

    @Test
    public void CodeTestApi_요청응답시간테스트() throws InterruptedException {

        String userId = "hong";
        String name = "홍길동";
        Long memberId = 1l;
        String regNo = "860824-1655068";

        ScrapRequest request = new ScrapRequest(name, regNo);
        MemberResponse memberResponse = MemberResponse.builder()
                .id(memberId)
                .userId(userId)
                .regNo(regNo)
                .name(name).build();

        Mockito.when(memberService.getById(memberId))
                .thenReturn(memberResponse);

        final int timeoutSec = 21;
        final Boolean[] whetherToRespond = new Boolean[1];
        whetherToRespond[0] = false;

        codeTest3o3ApiService.getScrapByNameAndRegNo(request,
                new CustomCallback<ScrapResponse>() {

                    @Override
                    public void onResponse(Call<ScrapResponse> call, Response<ScrapResponse> response) {
                        super.onResponse(call, response);
                        whetherToRespond[0] = true;
                        log.info(response.body().toString());
                    }

                    @Override
                    public void onFailure(Call<ScrapResponse> call, Throwable t) {
                        super.onFailure(call, t);
                        whetherToRespond[0] = true;
                        log.error("request scrap fail"  + t.getMessage());
                    }
                });


        for (Integer i = 0; i < timeoutSec; i++) {

            Thread.sleep(1000);

            log.info(i.toString());

            if(whetherToRespond[0]){
                break;
            }
        }

        Assertions.assertTrue(whetherToRespond[0]);
    }
}