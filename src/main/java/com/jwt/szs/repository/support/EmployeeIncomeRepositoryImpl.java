package com.jwt.szs.repository.support;

import com.jwt.szs.model.entity.EmployeeIncome;
import com.jwt.szs.model.entity.QEmployeeIncome;
import com.jwt.szs.repository.support.custom.EmployeeIncomeRepositoryCustom;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class EmployeeIncomeRepositoryImpl extends QuerydslRepositorySupportBasic implements EmployeeIncomeRepositoryCustom {

    private final QEmployeeIncome qEmployeeIncome = QEmployeeIncome.employeeIncome;

    public EmployeeIncomeRepositoryImpl() {
        super(EmployeeIncome.class);
    }

    @Override
    public Optional<EmployeeIncome> findByMemberId(Long memberId) {

        EmployeeIncome employeeIncome = getQueryFactory()
                .selectFrom(qEmployeeIncome)
                .where(
                        qEmployeeIncome.memberId.eq(memberId)
                ).fetchFirst();

        return Optional.ofNullable(employeeIncome);
    }

}
