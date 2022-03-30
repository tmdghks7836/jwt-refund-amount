package com.jwt.szs.model.entity;

import com.jwt.szs.api.codetest3o3.model.type.ScrapRequestStatus;
import com.jwt.szs.model.dto.EmployeeIncomeCreationRequest;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

//TODO
@Entity
@Table(name = "member_sign_up_status")
@Getter
@Setter(AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberSignUpStatus extends BaseDateTime {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    private Member member;

    //기업명
    @Column(nullable = false)
    private ScrapRequestStatus status;
//
//    @Column(nullable = false)
//    private LocalDate

    public MemberSignUpStatus(Member member, EmployeeIncomeCreationRequest creationRequest) {

        this.member = member;
//        this.calculatedTax = creationRequest.getCalculatedTax();
//        this.companyName = creationRequest.getCompanyName();
//        this.paymentAmount = creationRequest.getPaymentAmount();
//        this.businessStartDate = creationRequest.getBusinessStartDate();
//        this.businessEndDate = creationRequest.getBusinessEndDate();
//        this.paymentDate = creationRequest.getPaymentDate();
    }
}
