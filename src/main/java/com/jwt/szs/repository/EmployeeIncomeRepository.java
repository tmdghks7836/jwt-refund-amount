package com.jwt.szs.repository;

import com.jwt.szs.model.entity.EmployeeIncome;
import com.jwt.szs.model.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeIncomeRepository extends JpaRepository<EmployeeIncome, Long> {

}
