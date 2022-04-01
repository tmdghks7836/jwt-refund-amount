package com.jwt.szs.model.entity;

import com.jwt.szs.api.codetest3o3.model.type.MemberScrapStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

/**
 * 멤버 스크랩 요청 상태정보를 관리합니다.
 * */
@Entity
@Table(name = "member_scrap_status")
@Getter
@Setter(AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberScrapEvent extends BaseDateTime {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private MemberScrapStatus status;

    public MemberScrapEvent(Long memberId, MemberScrapStatus memberScrapStatus) {

        this.memberId = memberId;
        this.status = memberScrapStatus;
    }
}
