package com.jwt.szs.service;

import com.jwt.szs.model.dto.EmployeeIncomeCreationRequest;
import com.jwt.szs.model.dto.MemberResponse;
import com.jwt.szs.model.entity.EmployeeIncome;
import com.jwt.szs.model.entity.Member;
import com.jwt.szs.repository.EmployeeIncomeRepository;
import com.jwt.szs.utils.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeIncomeService {

    private final EmployeeIncomeRepository employeeIncomeRepository;

    @Transactional
    public void create(Member member, EmployeeIncomeCreationRequest creationRequest){

        EmployeeIncome employeeIncome = new EmployeeIncome(member, creationRequest);
        employeeIncomeRepository.save(employeeIncome);
    }
}
