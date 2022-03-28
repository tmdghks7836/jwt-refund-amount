//package com.jwt.szs.api.naver.interceptor;
//
//import kr.co.oasisbusiness.rvaiform.api.naver.NaverRealEstateCrawler;
//import okhttp3.Interceptor;
//import okhttp3.Request;
//
//import java.io.IOException;
//
//public class NaverRealEstateTokenInterceptor implements Interceptor {
//
//    private final String defaultAccessToken;
//
//    public NaverRealEstateTokenInterceptor(String defaultAccessToken){
//        this.defaultAccessToken = defaultAccessToken;
//    }
//
//    @Override
//    public okhttp3.Response intercept(Chain chain) throws IOException {
//
//        NaverRealEstateCrawler crawler = new NaverRealEstateCrawler();
//
//        Request newRequest  = chain.request().newBuilder()
//                .addHeader("Authorization",
//                        "Bearer ".concat(crawler.getAccessToken() != null ? crawler.getAccessToken() : defaultAccessToken))
//                .build();
//        return chain.proceed(newRequest);
//    }
//}