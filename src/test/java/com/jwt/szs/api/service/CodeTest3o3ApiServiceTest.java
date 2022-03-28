package com.jwt.szs.api.service;

import com.jwt.szs.api.codetest3o3.model.ScrapRequest;
import com.jwt.szs.api.codetest3o3.model.ScrapResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CodeTest3o3ApiServiceTest {

    @Autowired
    private CodeTest3o3ApiService codeTest3o3ApiService;

    @Test
    public void getScrapByUsernameAndRegNo() throws InterruptedException {

        ScrapRequest request = new ScrapRequest("홍길동", "860824-1655068");
        codeTest3o3ApiService.getScrapByUsernameAndRegNo(request);

        for (int i = 0; i < 20; i++) {

            Thread.sleep(1000);
        }

//        ScrapResponse.EmployeeData employeeData = response.getEmployeeData();
//
//        log.info(response.toString());
    }
}