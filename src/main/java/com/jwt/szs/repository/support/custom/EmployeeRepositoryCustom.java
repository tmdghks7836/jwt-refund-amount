package com.jwt.szs.repository.support.custom;

import com.jwt.szs.model.entity.EmployeeIncome;
import com.jwt.szs.model.entity.Member;

import java.util.Optional;

public interface EmployeeRepositoryCustom {

    Optional<EmployeeIncome> findByMemberId(Long memberId);
}
