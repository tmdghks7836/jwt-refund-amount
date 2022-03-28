package com.jwt.szs.api.codetest3o3;

import com.jwt.szs.api.codetest3o3.model.ScrapRequest;
import com.jwt.szs.api.codetest3o3.model.ScrapResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface CodeTest3o3Api {

    @POST("scrap")
    Call<ScrapResponse> getScrapByUsernameAndRegNo(@Body ScrapRequest request);
}
