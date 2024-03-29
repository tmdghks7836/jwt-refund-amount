package com.jwt.szs.api.codetest3o3.config;


import com.jwt.szs.api.codetest3o3.CodeTest3o3Api;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

@Configuration
public class CodeTest3o3ApiConfig {

    @Value("${api.test-3o3.url}")
    private String codeTest3o3Url;

    @Value("${api.test-3o3.timeout}")
    private Integer test3o3ApiTimeout;

    @Bean
    public OkHttpClient okHttpClient() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        return new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .connectTimeout(test3o3ApiTimeout, TimeUnit.SECONDS)
                .writeTimeout(test3o3ApiTimeout, TimeUnit.SECONDS)
                .readTimeout(test3o3ApiTimeout, TimeUnit.SECONDS)
                .build();
    }

    @Bean
    public Retrofit retrofit(OkHttpClient client) {

        return new Retrofit.Builder().baseUrl(codeTest3o3Url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client).build();
    }

    @Bean
    public CodeTest3o3Api naverEstateApi(Retrofit retrofit) {
        return retrofit.create(CodeTest3o3Api.class);
    }
}
