package com.jwt.szs.repository;

import com.jwt.szs.model.entity.EmployeeIncome;
import com.jwt.szs.repository.support.custom.EmployeeIncomeRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeIncomeRepository extends JpaRepository<EmployeeIncome, Long>, EmployeeIncomeRepositoryCustom {

}
