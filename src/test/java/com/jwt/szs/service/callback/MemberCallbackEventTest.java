package com.jwt.szs.service.callback;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MemberCallbackEventTest {

    @Autowired
    private MemberCallbackEvent memberCallbackEvent;

    @Test
    void signUpCallback() {

    }

    @Test
    void getScrapResponseCallback() {
    }
}