package com.jwt.szs.repository;

import com.jwt.szs.model.entity.Member;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void 멤버_저장() {

        Member member = new Member("hong123", "홍길동",
                "143241-4323423", "123");

        Member savedMember = memberRepository.save(member);

        Assertions.assertNotNull(savedMember.getId());
        Assertions.assertEquals("hong123", savedMember.getUserId());
        Assertions.assertEquals("홍길동", savedMember.getName());
        Assertions.assertEquals("143241-4323423", savedMember.getRegNo());
        Assertions.assertEquals("123", savedMember.getPassword());
    }


}