package com.jwt.szs.service.member;

import com.jwt.szs.repository.EmployeeIncomeRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MemberServiceTest {

    @Mock
    EmployeeIncomeRepository employeeIncomeRepository;

    @InjectMocks
    MemberService memberService;

    @Test
    void getByUserId() {
    }

    @Test
    void getByUserIdAndPassword() {
    }

    @Test
    void asyncSignUp() {
    }

    @Test
    void getById() {
    }

    @Test
    void scrap() {

//        Mockito.when(employeeIncomeRepository.findByMember(null))

//        memberService.scrap();
    }

    @Test
    void getRefundInformation() {
    }
}