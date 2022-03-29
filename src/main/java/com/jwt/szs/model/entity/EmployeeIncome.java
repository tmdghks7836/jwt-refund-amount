package com.jwt.szs.model.entity;

import com.jwt.szs.model.dto.EmployeeIncomeCreationRequest;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "employ_income")
@Getter
@Setter(AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmployeeIncome extends BaseDateTime {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    private Member member;

    //기업명
    @Column(nullable = false)
    private String companyName;

    //총 지급액
    @Column(nullable = false)
    private Long paymentAmount;

    //산출세액
    @Column(nullable = false)
    private Long calculatedTax;

    //업무 시작일
    @Column(nullable = false)
    private LocalDate businessStartDate;

    //업무 종료일
    @Column(nullable = false)
    private LocalDate businessEndDate;

    //지급일
    @Column(nullable = false)
    private LocalDate paymentDate;

    public EmployeeIncome(Member member, EmployeeIncomeCreationRequest creationRequest) {

        this.member = member;
        this.calculatedTax = creationRequest.getCalculatedTax();
        this.companyName = creationRequest.getCompanyName();
        this.paymentAmount = creationRequest.getPaymentAmount();
        this.businessStartDate = creationRequest.getBusinessStartDate();
        this.businessEndDate = creationRequest.getBusinessEndDate();
        this.paymentDate = creationRequest.getPaymentDate();
    }
}
