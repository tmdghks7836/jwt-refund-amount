package com.jwt.szs.repository.support.custom;

import com.jwt.szs.model.entity.EmployeeIncome;

import java.util.Optional;

public interface EmployeeIncomeRepositoryCustom {

    Optional<EmployeeIncome> findByMemberId(Long memberId);
}
