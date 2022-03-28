package com.jwt.szs.core;

import lombok.extern.slf4j.Slf4j;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Slf4j
public abstract class CustomCallback<T> implements Callback<T> {

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        log.info("response = " + response);
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        t.printStackTrace();
    }
}