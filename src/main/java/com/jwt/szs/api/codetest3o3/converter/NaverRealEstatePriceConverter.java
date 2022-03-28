//package com.jwt.szs.api.naver.converter;
//
//import kr.co.oasisbusiness.rvaiform.utils.RvaiNumberFormatUtils;
//
//public class NaverRealEstatePriceConverter {
//
//    public static long convertPrice(String price){
//
//        String million = "만";
//
//        //1억 5000 -> 1억 5000만
//        if (!price.contains(million)) {
//            price += million;
//        }
//
//        return RvaiNumberFormatUtils.stringWithNumberToLong(price);
//    }
//}
