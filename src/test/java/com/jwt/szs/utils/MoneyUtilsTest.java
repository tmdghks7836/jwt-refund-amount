package com.jwt.szs.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MoneyUtilsTest {

    @Test
    public void convert() {

        String convert1 = MoneyUtils.convertKorean(680150l);

        Assertions.assertEquals("68만 150원", convert1);

        String convert2 = MoneyUtils.convertKorean(110002331l);

        Assertions.assertEquals("1억 1000만 2천 331원", convert2);

        String convert3 = MoneyUtils.convertKorean(4318999000l);

        Assertions.assertEquals("43억 1899만 9천원", convert3);

        String convert4 = MoneyUtils.convertKorean(5000043099030l);

        Assertions.assertEquals("5조 4309만 9천 30원", convert4);

        String convert5 = MoneyUtils.convertKorean(-5000043099030l);

        Assertions.assertEquals("-5조 4309만 9천 30원", convert5);

        String convert6 = MoneyUtils.convertKorean(0l);

        Assertions.assertEquals("0원", convert6);

        String convert7 = MoneyUtils.convertKorean(10000l);

        Assertions.assertEquals("1만원", convert7);
    }
}