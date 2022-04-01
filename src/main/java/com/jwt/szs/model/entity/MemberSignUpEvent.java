package com.jwt.szs.model.entity;

import com.jwt.szs.api.codetest3o3.model.type.MemberScrapStatus;
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
public class MemberSignUpEvent extends BaseDateTime {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    private Member member;

    //기업명
    @Column(nullable = false)
    private MemberScrapStatus status;

    public MemberSignUpEvent(Member member) {

        this.member = member;
//        this.calculatedTax = creationRequest.getCalculatedTax();
//        this.companyName = creationRequest.getCompanyName();
//        this.paymentAmount = creationRequest.getPaymentAmount();
//        this.businessStartDate = creationRequest.getBusinessStartDate();
//        this.businessEndDate = creationRequest.getBusinessEndDate();
//        this.paymentDate = creationRequest.getPaymentDate();
    }
}
