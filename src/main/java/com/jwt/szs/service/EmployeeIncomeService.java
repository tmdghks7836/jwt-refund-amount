package com.jwt.szs.service;

import com.jwt.szs.exception.ResourceNotFoundException;
import com.jwt.szs.model.dto.EmployeeIncomeCreationRequest;
import com.jwt.szs.model.dto.EmployeeIncomeResponse;
import com.jwt.szs.model.entity.EmployeeIncome;
import com.jwt.szs.model.entity.Member;
import com.jwt.szs.repository.EmployeeIncomeRepository;
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

    private final RefundService refundService;

    @Transactional
    public void create(Member member, EmployeeIncomeCreationRequest creationRequest) {

        EmployeeIncome employeeIncome = new EmployeeIncome(member, creationRequest);
        employeeIncomeRepository.save(employeeIncome);
    }

    public EmployeeIncomeResponse getRefund(Member member) {

        EmployeeIncome employeeIncome = employeeIncomeRepository.findByMember(member)
                .orElseThrow(() -> new ResourceNotFoundException("not found member id : " + member.getId()));

        Long incomeTax = refundService.getIncomeTax(employeeIncome.getCalculatedTax());
        Long incomeTaxLimit = refundService.getIncomeTaxLimit(employeeIncome.getPaymentAmount());
        Long refundAmount = refundService.calculateAmount(employeeIncome.getPaymentAmount(), employeeIncome.getCalculatedTax());

        //TODO 금액 한국 한글 화폐단위로 변경
        return EmployeeIncomeResponse.builder()
                .name(employeeIncome.getMember().getName())
                .taxAmount(incomeTax.toString())
                .refundAmount(refundAmount.toString())
                .taxLimitAmount(incomeTaxLimit.toString()).build();
    }

}
