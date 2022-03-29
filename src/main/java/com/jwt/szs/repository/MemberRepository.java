package com.jwt.szs.repository;

import com.jwt.szs.model.entity.Member;
import com.jwt.szs.repository.support.custom.MemberRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

/**
 *  간단한 query 지만 Querydsl 도 사용해 보았습니다.
 *  Optional<Member> findByUserId(String userId);
 * */
}
