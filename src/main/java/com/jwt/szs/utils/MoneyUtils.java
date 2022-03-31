package com.jwt.szs.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class MoneyUtils {


    public static String convertKorean(final Long money) {

        long absMoney = Math.abs(money);

        String[] korean = {"만", "억", "조", "경"};
        Map<Integer, String> map = new HashMap<>();
        int maxDivision = 0;

        for (int i = 0; i < korean.length; i++) {

            maxDivision += 4;
            map.put(maxDivision, korean[i]);
        }

        StringBuilder sb = new StringBuilder();

        while (maxDivision > 0) {

            long num = (long) (absMoney / Math.pow(10, maxDivision)) % 10000;

            if (num > 0) {
                sb.append(num).append(map.get(maxDivision)).append(" ");
            }
            maxDivision -= 4;
        }

        long thousand = (long) (absMoney / Math.pow(10, 3) % 10);
        long remain = (long) (absMoney % Math.pow(10, 3));

        if (thousand > 0) {
            sb.append(thousand).append(remain > 0 ? "천 " : "천");
        }

        if (remain > 0) {
            sb.append(remain);
        }

        sb.append(money != 0 ? "원" : "0원");
        String result = money < 0 ? "-" + sb : sb.toString();
        log.info(result);
        return result;
    }
}