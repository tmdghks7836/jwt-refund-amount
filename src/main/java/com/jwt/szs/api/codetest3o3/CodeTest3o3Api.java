package com.jwt.szs.api.codetest3o3;

import com.jwt.szs.api.codetest3o3.model.NameWithRegNoDto;
import com.jwt.szs.api.codetest3o3.model.ScrapResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface CodeTest3o3Api {

    @POST("scrap")
    Call<ScrapResponse> getScrapByNameAndRegNo(@Body NameWithRegNoDto request);
}
