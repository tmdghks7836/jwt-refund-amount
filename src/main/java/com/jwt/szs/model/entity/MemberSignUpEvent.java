package com.jwt.szs.model.entity;

import com.jwt.szs.api.codetest3o3.model.type.MemberSignUpStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Table(name = "member_sign_up_event", indexes = {
        @Index(name = "member_sign_up_event_index", columnList = "createdAt, userId")
})
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

    @Column(length = 1000)
    private String message;

    public MemberSignUpEvent(String userId, String password, MemberSignUpStatus status) {

        this(userId, password, status, null);
    }

    public MemberSignUpEvent(String userId, String password, MemberSignUpStatus status, String message) {

        this.userId = userId;
        this.password = password;
        this.status = status;
        this.message = message;

    }

    public Boolean isPending() {

        return MemberSignUpStatus.PENDING.equals(status);
    }

    public Boolean isFailed() {

        return MemberSignUpStatus.FAILED.equals(status);
    }
}
