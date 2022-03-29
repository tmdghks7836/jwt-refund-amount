package com.jwt.szs.repository;

import com.jwt.szs.model.entity.EmployeeIncome;
import com.jwt.szs.model.entity.Member;
import com.jwt.szs.repository.support.custom.EmployeeRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeIncomeRepository extends JpaRepository<EmployeeIncome, Long>, EmployeeRepositoryCustom {

    /**
     *  간단한 query 지만 Querydsl 도 사용해 보았습니다.
     *    Optional<Member> findByMember(Member member);
     * */
}
