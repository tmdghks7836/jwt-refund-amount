//package com.jwt.szs.api.naver.converter;
//
//import kr.co.oasisbusiness.rvaiform.model.type.BuildingTradeType;
//import retrofit2.Converter;
//import retrofit2.Retrofit;
//
//import java.lang.annotation.Annotation;
//import java.lang.reflect.Type;
//
///**
// * 요청시 거래타입 enum binding
// * */
//public class BuildingTradeTypeConverterFactory extends Converter.Factory {
//
//    @Override
//    public Converter<, String> stringConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
//
//        Converter<BuildingTradeType, String> converter = null;
//
//        if(type.getTypeName().equals(BuildingTradeType.class.getTypeName())) {
//            converter = value -> value.getNaverApiCode();
//        }
//        return converter;
//    }
//}