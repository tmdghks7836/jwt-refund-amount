package com.jwt.szs.repository.support;

import com.jwt.szs.model.entity.EmployeeIncome;
import com.jwt.szs.model.entity.Member;
import com.jwt.szs.model.entity.QEmployeeIncome;
import com.jwt.szs.repository.support.custom.EmployeeRepositoryCustom;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Repository
public class EmployeeRepositoryImpl extends QuerydslRepositorySupportBasic implements EmployeeRepositoryCustom {

    private final QEmployeeIncome qEmployeeIncome = QEmployeeIncome.employeeIncome;

    public EmployeeRepositoryImpl() {
        super(EmployeeIncome.class);
    }

    @Override
    public Optional<EmployeeIncome> findByMember(Member member) {

        EmployeeIncome employeeIncome = getQueryFactory()
                .selectFrom(qEmployeeIncome)
                .where(
                        memberEq(member)
                ).fetchOne();

        return Optional.ofNullable(employeeIncome);
    }

    public BooleanExpression memberEq(Member member) {
        return ObjectUtils.isEmpty(member) ? null : qEmployeeIncome.member.eq(member);
    }
}
