package com.jwt.szs.model.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Table(name = "member")
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AvailableSignUpMember extends BaseDateTime {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(length = 50)
    private Long id;

    @Column(length = 50, unique = true)
    private String name;


}
