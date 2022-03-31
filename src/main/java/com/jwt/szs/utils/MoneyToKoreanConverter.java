package com.jwt.szs.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class MoneyToKoreanConverter {


    public String convert(final Long money) {

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
                sb.append(num + map.get(maxDivision));
            }
            maxDivision -= 4;
        }

        long thousand = (long) (absMoney / Math.pow(10, 3) % 10);
        if (thousand > 0) {
            sb.append(thousand).append("천");
        }

        long remain = (long) (absMoney % Math.pow(10, 3));
        if (remain > 0) {
            sb.append(remain);
        }

        sb.append(money != 0 ? "원" : "0원");
        String result = money < 0 ? "-" + sb : sb.toString();
        log.info(result);
        return result;
    }
}
