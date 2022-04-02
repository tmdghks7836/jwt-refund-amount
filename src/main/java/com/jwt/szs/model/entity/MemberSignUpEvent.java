package com.jwt.szs.model.entity;

import com.jwt.szs.api.codetest3o3.model.type.MemberScrapStatus;
import com.jwt.szs.api.codetest3o3.model.type.MemberSignUpStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

//TODO
@Entity
@Table(name = "member_sign_up_evet")
@Getter
@Setter(AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberSignUpEvent extends BaseDateTime {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private MemberSignUpStatus status;

    public MemberSignUpEvent(String userId, String password) {

        this.userId = userId;
        this.password = password;
        this.status = MemberSignUpStatus.PENDING;
    }
}
