package com.jwt.szs.model.entity;

import com.jwt.szs.model.base.BaseMember;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;

@Entity
@Table(name = "member")
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseDateTime implements BaseMember {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length = 50, unique = true)
    private String userId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(length = 14,nullable = false)
    private String regNo;


    public Member(String userId, String name, String regNo ,String password) {
        this.userId = userId;
        this.name = name;
        this.regNo = regNo;
        this.password = password;
    }
}
