package com.jwt.szs.model.entity;

import com.jwt.szs.model.base.BaseMember;
import com.jwt.szs.model.entity.converter.EncryptionFieldConverter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Table(name = "member")
@Getter
@Setter(value = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseDateTime implements BaseMember {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    //TODO userId 지만 id와 혼란이 있을 여지가 있다. username ?
    @Column(length = 50, unique = true)
    private String userId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Convert(converter = EncryptionFieldConverter.class)
    @Column(nullable = false)
    private String regNo;


    public Member(String userId, String name, String regNo, String password) {
        this.userId = userId;
        this.name = name;
        this.regNo = regNo;
        this.password = password;
    }
}
