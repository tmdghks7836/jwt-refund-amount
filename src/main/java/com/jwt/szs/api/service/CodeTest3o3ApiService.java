package com.jwt.szs.api.service;

import com.jwt.szs.api.codetest3o3.CodeTest3o3Api;
import com.jwt.szs.api.codetest3o3.model.ScrapRequest;
import com.jwt.szs.api.codetest3o3.model.ScrapResponse;
import com.jwt.szs.core.CustomCallback;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Response;

@Slf4j
@Service
@RequiredArgsConstructor
public class CodeTest3o3ApiService {

    private final CodeTest3o3Api codeTest3o3Api;

    public void getScrapByNameAndRegNo(ScrapRequest request) {

        Call<ScrapResponse> call = codeTest3o3Api.getScrapByNameAndRegNo(request);



        call.enqueue(new CustomCallback<ScrapResponse>() {

            @Override
            public void onResponse(Call<ScrapResponse> call, Response<ScrapResponse> response) {
                super.onResponse(call, response);

                if(response.isSuccessful()){

                }
            }

            @Override
            public void onFailure(Call<ScrapResponse> call, Throwable t) {
                super.onFailure(call, t);
            }
        });
    }
}
