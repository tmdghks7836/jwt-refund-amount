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
        int maxSquared = 0;

        for (int i = 0; i < korean.length; i++) {

            maxSquared += 4;
            map.put(maxSquared, korean[i]);
        }

        StringBuilder sb = new StringBuilder();
        boolean appendStarted = false;
        while (maxSquared > 0) {

            long num = (long) (absMoney / Math.pow(10, maxSquared)) % 10000;

            if (num > 0) {

                appendSpace(appendStarted, sb);
                sb.append(num).append(map.get(maxSquared));
                appendStarted = true;
            }
            maxSquared -= 4;
        }

        long thousand = (long) (absMoney / Math.pow(10, 3) % 10);
        long remain = (long) (absMoney % Math.pow(10, 3));

        if (thousand > 0) {

            appendSpace(appendStarted, sb);
            sb.append(thousand).append("천");
        }

        if (remain > 0) {

            appendSpace(appendStarted, sb);
            sb.append(remain);
        }

        sb.append(money != 0 ? "원" : "0원");
        String result = money < 0 ? "-" + sb : sb.toString();
        log.info(result);
        return result;
    }

    private static void appendSpace(Boolean appendStarted, StringBuilder sb){

        if(appendStarted){
            sb.append(" ");
        }
    }
}
