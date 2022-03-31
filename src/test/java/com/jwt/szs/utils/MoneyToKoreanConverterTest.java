package com.jwt.szs.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MoneyToKoreanConverterTest {

    @Autowired
    MoneyToKoreanConverter moneyToKoreanConverter;

    @Test
    public void convert() {

        String convert1 = moneyToKoreanConverter.convert(680150l);

        Assertions.assertEquals("68만150원", convert1);

        String convert2 = moneyToKoreanConverter.convert(110002331l);

        Assertions.assertEquals("1억1000만2천331원", convert2);


        String convert3 = moneyToKoreanConverter.convert(4318999000l);

        Assertions.assertEquals("43억1899만9천원", convert3);

        String convert4 = moneyToKoreanConverter.convert(5000043099030l);

        Assertions.assertEquals("5조4309만9천30원", convert4);

        String convert5 = moneyToKoreanConverter.convert(-5000043099030l);

        Assertions.assertEquals("-5조4309만9천30원", convert5);

        String convert6 = moneyToKoreanConverter.convert(0l);

        Assertions.assertEquals("0원", convert6);

    }
}