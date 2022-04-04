package com.jwt.szs.service;

import com.jwt.szs.exception.ResourceNotFoundException;
import com.jwt.szs.model.dto.EmployeeIncomeCreationRequest;
import com.jwt.szs.model.dto.EmployeeIncomeResponse;
import com.jwt.szs.model.entity.EmployeeIncome;
import com.jwt.szs.model.entity.Member;
import com.jwt.szs.repository.EmployeeIncomeRepository;
import com.jwt.szs.service.member.MemberScrapEventService;
import com.jwt.szs.service.member.MemberService;
import com.jwt.szs.utils.MoneyUtils;
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

    private final MemberScrapEventService memberScrapEventService;

    @Transactional
    public void upsert(Long memberId, EmployeeIncomeCreationRequest creationRequest) {

        log.info("근로소득 정보를 저장합니다. 기존에 가지고 있다면 업데이트 합니다.");

        memberScrapEventService.requestComplete(memberId);
        employeeIncomeRepository.findByMemberId(memberId)
                .ifPresentOrElse(employeeIncome -> {

                    employeeIncome.changeInfo(creationRequest);
                }, () -> {

                    EmployeeIncome employeeIncome = new EmployeeIncome(memberId, creationRequest);
                    employeeIncomeRepository.save(employeeIncome);
                });
    }

    public EmployeeIncomeResponse getByMember(Member member) {

        EmployeeIncome employeeIncome = employeeIncomeRepository.findByMemberId(member.getId())
                .orElseThrow(() -> new ResourceNotFoundException("not found member id : " + member.getId()));

        Long incomeTax = refundService.getIncomeTax(employeeIncome.getCalculatedTax());
        Long incomeTaxLimit = refundService.getIncomeTaxLimit(employeeIncome.getPaymentAmount());
        Long refundAmount = refundService.calculateAmount(employeeIncome.getPaymentAmount(), employeeIncome.getCalculatedTax());

        return EmployeeIncomeResponse.builder()
                .name(member.getName())
                .taxAmount(MoneyUtils.convertKorean(incomeTax))
                .taxLimitAmount(MoneyUtils.convertKorean(incomeTaxLimit))
                .refundAmount(MoneyUtils.convertKorean(refundAmount))
                .build();
    }

    public Boolean isPresent(Long memberId) {

        return employeeIncomeRepository.findByMemberId(memberId).isPresent();
    }

}
